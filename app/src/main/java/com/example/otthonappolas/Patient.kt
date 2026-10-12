package com.example.otthonappolas


data class Patient(
    val id: String,
    val name: String,
    val address: String,
    val scheduledTime: String
)

val dummyPatients = listOf(
    Patient("1", "Kovács János", "1055 Budapest, Kossuth tér 1.", "08:00"),
    Patient("2", "Nagy Éva", "1111 Budapest, Műegyetem rkp. 3.", "10:30"),
    Patient("3", "Szabó Ilona", "1132 Budapest, Váci út 12.", "13:00")
)