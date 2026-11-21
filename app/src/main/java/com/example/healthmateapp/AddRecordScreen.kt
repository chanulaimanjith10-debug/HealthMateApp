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
    modifier: Modifier = Modifier,
    initialRecord: HealthRecord? = null,
    onSave: (HealthRecord) -> Boolean
) {
    var steps by remember(initialRecord) {
        mutableStateOf(initialRecord?.steps?.toString() ?: "")
    }

    var calories by remember(initialRecord) {
        mutableStateOf(initialRecord?.calories?.toString() ?: "")
    }

    var water by remember(initialRecord) {
        mutableStateOf(initialRecord?.water?.toString() ?: "")
    }

    var message by remember(initialRecord) {
        mutableStateOf("")
    }

    val today = remember {
        SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(Date())
    }

    val recordDate = initialRecord?.date ?: today

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (initialRecord == null) {
                "Add Health Record"
            } else {
                "Edit Health Record"
            },
            style = MaterialTheme.typography.headlineSmall
        )

        Text(text = "Date: $recordDate")

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
                            id = initialRecord?.id ?: 0,
                            date = recordDate,
                            steps = stepsValue,
                            calories = caloriesValue,
                            water = waterValue
                        )

                        val saved = onSave(record)

                        when {
                            saved && initialRecord == null ->
                                "Record saved."

                            saved ->
                                "Record updated."

                            else ->
                                "This date already has a record. Edit it from View Records."
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (initialRecord == null) {
                    "Save Record"
                } else {
                    "Update Record"
                }
            )
        }

        if (message.isNotEmpty()) {
            Text(text = message)
        }

    }
}
