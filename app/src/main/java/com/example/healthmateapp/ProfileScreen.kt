package com.example.healthmateapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HealthMateBottomBar(
    selectedScreen: String,
    onNavigate: (String) -> Unit
) {
    val destinations = listOf(
        Triple("home", "Home", "🏠"),
        Triple("records", "Records", "📅"),
        Triple("add", "Add", "➕"),
        Triple("profile", "Profile", "👤")
    )

    NavigationBar {
        destinations.forEach { destination ->
            val route = destination.first
            val title = destination.second
            val symbol = destination.third

            NavigationBarItem(
                selected = if (selectedScreen == "about") {
                    route == "profile"
                } else if (selectedScreen == "edit") {
                    route == "add"
                } else {
                    selectedScreen == route
                },
                onClick = {
                    onNavigate(route)
                },
                icon = {
                    Text(text = symbol)
                },
                label = {
                    Text(text = title)
                }
            )
        }
    }
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onAboutClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleLarge
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (darkMode) "Dark mode" else "Light mode",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = "Choose the app appearance")
                }

                Switch(
                    checked = darkMode,
                    onCheckedChange = onDarkModeChange
                )
            }
        }

        Button(
            onClick = onAboutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "About HealthMate")
        }

        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Log Out")
        }
    }
}

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "About HealthMate",
            style = MaterialTheme.typography.headlineMedium
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "HealthMate",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(text = "Developed by Chanula Imanjith")
            }
        }

        Button(onClick = onBackClick) {
            Text(text = "Back")
        }
    }
}
