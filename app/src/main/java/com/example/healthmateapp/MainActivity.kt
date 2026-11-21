package com.example.healthmateapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.healthmateapp.data.HealthDatabase
import com.example.healthmateapp.data.HealthRecord
import com.example.healthmateapp.ui.theme.HealthMateAppTheme
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HealthMateAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    HealthMateApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun HealthMateApp(
    modifier: Modifier = Modifier
) {
    val auth = remember {
        FirebaseAuth.getInstance()
    }

    var isLoggedIn by remember {
        mutableStateOf(auth.currentUser != null)
    }

    if (isLoggedIn) {
        HealthMateContent(
            modifier = modifier,
            onLogout = {
                auth.signOut()
                isLoggedIn = false
            }
        )
    } else {
        LoginScreen(
            modifier = modifier,
            onLoginSuccess = {
                isLoggedIn = true
            }
        )
    }
}

@Composable
fun HealthMateContent(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    var screen by remember {
        mutableStateOf("home")
    }

    var selectedRecord by remember {
        mutableStateOf<HealthRecord?>(null)
    }

    val context = LocalContext.current

    val database = remember {
        HealthDatabase(context.applicationContext)
    }

    var records by remember {
        mutableStateOf(database.getAllRecords())
    }

    val today = remember {
        SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(Date())
    }

    val todayRecord = records
        .filter { it.date == today }
        .maxByOrNull { it.id }

    when (screen) {
        "add", "edit" -> {
            val currentRecord = selectedRecord.takeIf {
                screen == "edit"
            }

            AddRecordScreen(
                modifier = modifier,
                initialRecord = currentRecord,
                onBack = {
                    screen = if (currentRecord == null) {
                        "home"
                    } else {
                        "records"
                    }
                },
                onSave = { record ->
                    val saved = if (currentRecord == null) {
                        // Do not add another record for the same date.
                        val oldRecord =
                            database.getRecordByDate(record.date)

                        if (oldRecord == null) {
                            database.insertRecord(record) != -1L
                        } else {
                            false
                        }
                    } else {
                        // Update the selected record.
                        database.updateRecord(record) > 0
                    }

                    if (saved) {
                        records = database.getAllRecords()
                    }

                    saved
                }
            )
        }

        "records" -> {
            HealthRecordListScreen(
                records = records,
                modifier = modifier,
                onBack = {
                    screen = "home"
                },
                onEdit = { record ->
                    selectedRecord = record
                    screen = "edit"
                },
                onDelete = { id ->
                    database.deleteRecord(id)
                    records = database.getAllRecords()
                }
            )
        }

        else -> {
            DashboardScreen(
                modifier = modifier,
                steps = todayRecord?.steps ?: 0,
                calories = todayRecord?.calories ?: 0,
                water = todayRecord?.water ?: 0,
                recentRecords = records,
                onAddRecord = {
                    selectedRecord = null
                    screen = "add"
                },
                onViewRecords = {
                    records = database.getAllRecords()
                    screen = "records"
                },
                onLogout = onLogout
            )
        }
    }
}

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    steps: Int,
    calories: Int,
    water: Int,
    recentRecords: List<HealthRecord>,
    onAddRecord: () -> Unit,
    onViewRecords: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "HealthMate",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Your daily health overview",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Today's Activity",
            style = MaterialTheme.typography.titleLarge
        )

        HealthMetricCard(
            title = "Steps",
            value = steps.toString(),
            unit = "steps"
        )

        HealthMetricCard(
            title = "Calories burned",
            value = calories.toString(),
            unit = "kcal"
        )

        HealthMetricCard(
            title = "Water intake",
            value = water.toString(),
            unit = "ml"
        )

        StepsChart(records = recentRecords)

        Button(
            onClick = onAddRecord,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Record")
        }

        Button(
            onClick = onViewRecords,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Records")
        }

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log Out")
        }
    }
}

@Composable
fun StepsChart(records: List<HealthRecord>) {
    val dateFormatter = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.US)
    }

    val days = remember {
        (6 downTo 0).map { daysAgo ->
            Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -daysAgo)
            }.time
        }
    }

    val stepsByDate = records.associate {
        it.date to it.steps
    }

    val values = days.map { date ->
        stepsByDate[dateFormatter.format(date)] ?: 0
    }

    val maxSteps = (values.maxOrNull() ?: 0)
        .coerceAtLeast(1)
        .toFloat()

    val lineColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Steps - Last 7 Days",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val left = 8.dp.toPx()
                val right = size.width - 8.dp.toPx()
                val top = 8.dp.toPx()
                val bottom = size.height - 8.dp.toPx()

                val chartWidth = right - left
                val chartHeight = bottom - top

                for (line in 0..3) {
                    val y = top + chartHeight * line / 3f

                    drawLine(
                        color = gridColor,
                        start = Offset(left, y),
                        end = Offset(right, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                val points = values.mapIndexed { index, value ->
                    val x = left +
                            chartWidth * index / (values.size - 1).toFloat()

                    val y = bottom -
                            (value / maxSteps) * chartHeight

                    Offset(x, y)
                }

                if (points.size > 1) {
                    val path = Path().apply {
                        moveTo(
                            points.first().x,
                            points.first().y
                        )

                        points.drop(1).forEach { point ->
                            lineTo(point.x, point.y)
                        }
                    }

                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 3.dp.toPx())
                    )
                }

                points.forEach { point ->
                    drawCircle(
                        color = lineColor,
                        radius = 4.dp.toPx(),
                        center = point
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEach { date ->
                    Text(
                        text = SimpleDateFormat(
                            "d",
                            Locale.US
                        ).format(date),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun HealthMetricCard(
    title: String,
    value: String,
    unit: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "$value $unit",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    HealthMateAppTheme {
        DashboardScreen(
            steps = 0,
            calories = 0,
            water = 0,
            recentRecords = emptyList(),
            onAddRecord = {},
            onViewRecords = {},
            onLogout = {}
        )
    }
}