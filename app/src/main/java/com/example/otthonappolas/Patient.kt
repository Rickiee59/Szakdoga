package com.example.otthonappolas

import androidx.compose.runtime.mutableStateListOf

data class Patient(
    val id: String,
    val name: String,
    val address: String,
    val scheduledTime: String,
    val phone: String = "+36 30 123 4567",
    val email: String = "hozzatartozo@pelda.hu", // Ide megy majd a PDF!
    val description: String = "Általános állapotfelmérés, vérnyomásmérés, gyógyszeradagolás ellenőrzése."
)

data class CompletedShift(
    val patient: Patient,
    val date: String,
    val startTime: String,
    val endTime: String,
    val nurseWage: Int,
    val patientCost: Int,
    val pdfBytes: ByteArray? = null, // A generált PDF nyers adatai a feltöltéshez
    val receiptPdfUrl: String = "Felhőbe szinkronizálva" // Az adatbázisban tárolt link
)

val activePatients = mutableStateListOf(
    Patient("1", "Kiss Mária", "6720 Szeged, Kárász utca 5.", "08:00", "+36 20 555 1122", "kiss.csalad@gmail.com", "Inzulin beadása, vércukorszint mérés és sebkötözés."),
    Patient("2", "Tóth Zoltán", "6724 Szeged, Rókusi körút 12.", "10:30", "+36 30 444 3322", "toth.zoltanne@freemail.hu", "Gyógytorna átmozgatás, vérnyomás ellenőrzés."),
    Patient("3", "Horváth Anna", "6721 Szeged, Felső Tisza-part 31.", "13:00", "+36 70 999 8877", "horvath.peter@gmail.com", "Infúzió bekötése, általános megfigyelés."),
    Patient("4", "Varga István", "6725 Szeged, Szentháromság utca 45.", "15:00", "+36 30 111 2233", "varga.istvan@citromail.hu", "Műtét utáni sebkezelés, kötéscsere.")
)

val completedShifts = mutableStateListOf(
    CompletedShift(
        patient = Patient("0", "Németh László", "6722 Szeged, Tisza Lajos krt. 50.", "09:00", email = "nemeth.laszlo@gmail.com"),
        date = "2026-10-20",
        startTime = "09:05",
        endTime = "10:05",
        nurseWage = 4500,
        patientCost = 6500
    )
)