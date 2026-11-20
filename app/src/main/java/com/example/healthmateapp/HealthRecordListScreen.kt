package com.example.healthmateapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.healthmateapp.data.HealthRecord

@Composable
fun HealthRecordListScreen(
    records: List<HealthRecord>,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onEdit: (HealthRecord) -> Unit,
    onDelete: (Int) -> Unit
) {
    var searchDate by remember {
        mutableStateOf("")
    }

    var deleteId by remember {
        mutableStateOf<Int?>(null)
    }

    val filteredRecords = if (searchDate.isBlank()) {
        records
    } else {
        records.filter {
            it.date.contains(searchDate.trim())
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Health Records",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = searchDate,
            onValueChange = {
                searchDate = it
            },
            label = {
                Text("Search date (yyyy-MM-dd)")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }

        if (filteredRecords.isEmpty()) {
            Text(
                text = if (records.isEmpty()) {
                    "No records yet."
                } else {
                    "No records for this date."
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = filteredRecords,
                    key = { record -> record.id }
                ) { record ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Date: ${record.date}")
                            Text("Steps: ${record.steps}")
                            Text("Calories: ${record.calories} kcal")
                            Text("Water: ${record.water} ml")

                            Row(
                                horizontalArrangement =
                                    Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        onEdit(record)
                                    }
                                ) {
                                    Text("Edit")
                                }

                                Button(
                                    onClick = {
                                        deleteId = record.id
                                    }
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (deleteId != null) {
        AlertDialog(
            onDismissRequest = {
                deleteId = null
            },
            title = {
                Text("Delete record?")
            },
            text = {
                Text("This record will be deleted.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteId?.let { id ->
                            onDelete(id)
                        }
                        deleteId = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        deleteId = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}