package com.example.otthonappolas


data class Patient(
    val id: String,
    val name: String,
    val address: String,
    val scheduledTime: String
)

val dummyPatients = listOf(
    Patient("1", "Kiss Mária", "6720 Szeged, Kárász utca 5.", "08:00"),
    Patient("2", "Tóth Zoltán", "6724 Szeged, Rókusi körút 12.", "10:30"),
    Patient("3", "Horváth Anna", "6721 Szeged, Felső Tisza-part 31.", "13:00"),
    Patient("4", "Varga István", "6725 Szeged, Szentháromság utca 45.", "15:00")
)