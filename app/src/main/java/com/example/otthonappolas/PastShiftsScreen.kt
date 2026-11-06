package com.example.otthonappolas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PastShiftsScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    val filteredShifts = completedShifts.filter {
        it.patient.name.contains(searchQuery, ignoreCase = true) ||
                it.date.contains(searchQuery)
    }

    val totalWage = filteredShifts.sumOf { it.nurseWage }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Összesített kereset:", style = MaterialTheme.typography.labelLarge)
                Text(text = "$totalWage Ft", style = MaterialTheme.typography.headlineMedium)
                Text(text = "Elvégzett kezelések: ${filteredShifts.size} db", style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Szűrés név vagy dátum (ÉÉÉÉ-HH-NN) alapján") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredShifts) { shift ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = shift.patient.name, style = MaterialTheme.typography.titleMedium)
                            Text(text = shift.date, style = MaterialTheme.typography.labelMedium)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = shift.patient.address, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Idősáv: ${shift.startTime} - ${shift.endTime}", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "+${shift.nurseWage} Ft",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bizonylat: Adatbázisban rögzítve (${shift.patient.email})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { sendReceiptEmail(context, shift) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Bizonylat újraküldése emailben")
                    }
                }
            }
        }
    }
}