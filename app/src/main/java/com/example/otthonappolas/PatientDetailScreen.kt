package com.example.otthonappolas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun PatientDetailScreen(patientId: String, onBackClick: () -> Unit) {
    val patient = activePatients.find { it.id == patientId }
    val context = androidx.compose.ui.platform.LocalContext.current

    var isTreatmentStarted by remember { mutableStateOf(false) }
    var startTimeString by remember { mutableStateOf("") }
    var showSignatureDialog by remember { mutableStateOf(false) }

    if (patient == null) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("A műszak már lezárult vagy a beteg nem található.")
        }
        return
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        TextButton(onClick = onBackClick) { Text("< Vissza a listához") }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = patient.name, style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Cím: ${patient.address}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Telefonszám: ${patient.phone}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Tervezett érkezés: ${patient.scheduledTime}", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Kezelési leírás / Feladatok:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = patient.description, style = MaterialTheme.typography.bodyMedium)

        if (isTreatmentStarted) {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Kezelés folyamatban! Kezdés: $startTimeString",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (!isTreatmentStarted) {
            Button(
                onClick = {
                    isTreatmentStarted = true
                    startTimeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Megérkeztem (Kezelés indítása)")
            }
        } else {
            Button(
                onClick = { showSignatureDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Kezelés lezárása és aláírás")
            }
        }
    }

    if (showSignatureDialog) {
        SignatureDialog(
            onDismiss = { showSignatureDialog = false },
            onConfirm = {
                showSignatureDialog = false

                val endTimeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                val currentDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                val calendar = Calendar.getInstance()
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)

                val multiplier = if (isWeekend) 1.5 else 1.0
                val calculatedWage = (4500 * multiplier).toInt()
                val calculatedCost = (6500 * multiplier).toInt()


                val tempShift = CompletedShift(
                    patient = patient,
                    date = currentDateString,
                    startTime = startTimeString,
                    endTime = endTimeString,
                    nurseWage = calculatedWage,
                    patientCost = calculatedCost
                )
                val pdfBytes = generateReceiptPdfBytes(tempShift)
                val finalShift = tempShift.copy(pdfBytes = pdfBytes)

                completedShifts.add(0, finalShift)

                sendReceiptEmail(context, finalShift)

                activePatients.remove(patient)
                onBackClick()
            }
        )
    }
}