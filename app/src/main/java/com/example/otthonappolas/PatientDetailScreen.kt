package com.example.otthonappolas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PatientDetailScreen(patientId: String, onBackClick: () -> Unit) {
    val patient = dummyPatients.find { it.id == patientId }


    var showSignatureDialog by remember { mutableStateOf(false) }

    if (patient == null) {
        Text("Beteg nem található.")
        return
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        TextButton(onClick = onBackClick) { Text("< Vissza a listához") }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = patient.name, style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Cím: ${patient.address}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Tervezett érkezés: ${patient.scheduledTime}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Telefonszám: +36 30 123 4567")

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { showSignatureDialog = true },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Megérkeztem (Kezelés indítása)")
        }
    }


    if (showSignatureDialog) {
        SignatureDialog(
            onDismiss = { showSignatureDialog = false },
            onConfirm = {
                showSignatureDialog = false

                println("Kezelés lezárva, aláírás mentve!")
                onBackClick()
            }
        )
    }
}