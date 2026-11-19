package com.example.healthmateapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.healthmateapp.data.HealthDatabase
import com.example.healthmateapp.ui.theme.HealthMateAppTheme
import java.text.SimpleDateFormat
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
    var screen by remember {
        mutableStateOf("home")
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
        "add" -> {
            AddRecordScreen(
                onBack = {
                    screen = "home"
                },
                onSave = { record ->
                    val oldRecord =
                        database.getRecordByDate(record.date)

                    val saved = if (oldRecord == null) {
                        database.insertRecord(record) != -1L
                    } else {
                        database.updateRecord(
                            record.copy(id = oldRecord.id)
                        ) > 0
                    }

                    if (saved) {
                        records = database.getAllRecords()
                    }

                    saved
                }
            )
        }

        else -> {
            DashboardScreen(
                modifier = modifier,
                steps = todayRecord?.steps ?: 0,
                calories = todayRecord?.calories ?: 0,
                water = todayRecord?.water ?: 0,
                onAddRecord = {
                    screen = "add"
                }
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
    onAddRecord: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
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

        Button(
            onClick = onAddRecord,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Record")
        }

        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Records")
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
            onAddRecord = {}
        )
    }
}