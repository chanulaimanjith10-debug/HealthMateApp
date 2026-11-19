package com.example.healthmateapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.healthmateapp.data.HealthRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddRecordScreen(
    onBack: () -> Unit,
    onSave: (HealthRecord) -> Boolean
) {
    var steps by remember {
        mutableStateOf("")
    }

    var calories by remember {
        mutableStateOf("")
    }

    var water by remember {
        mutableStateOf("")
    }

    var message by remember {
        mutableStateOf("")
    }

    val today = remember {
        SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(Date())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Add Health Record",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Date: $today"
        )

        OutlinedTextField(
            value = steps,
            onValueChange = {
                steps = it.filter(Char::isDigit)
            },
            label = {
                Text("Steps")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = calories,
            onValueChange = {
                calories = it.filter(Char::isDigit)
            },
            label = {
                Text("Calories burned")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = water,
            onValueChange = {
                water = it.filter(Char::isDigit)
            },
            label = {
                Text("Water (ml)")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val stepsValue = steps.toIntOrNull()
                val caloriesValue = calories.toIntOrNull()
                val waterValue = water.toIntOrNull()

                message = when {
                    steps.isBlank() ||
                            calories.isBlank() ||
                            water.isBlank() -> {
                        "Please fill in all fields."
                    }

                    stepsValue == null ||
                            caloriesValue == null ||
                            waterValue == null -> {
                        "Enter valid numbers."
                    }

                    else -> {
                        val record = HealthRecord(
                            date = today,
                            steps = stepsValue,
                            calories = caloriesValue,
                            water = waterValue
                        )

                        if (onSave(record)) {
                            "Record saved."
                        } else {
                            "Could not save record."
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Record")
        }

        if (message.isNotEmpty()) {
            Text(text = message)
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}