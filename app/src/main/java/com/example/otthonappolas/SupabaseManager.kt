package com.example.otthonappolas

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

const val SUPABASE_URL = "https://ydbfmotxvxphgjhhigti.supabase.co"
const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlkYmZtb3R4dnhwaGdqaGhpZ3RpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTM5NzM0NzgsImV4cCI6MjEwOTU0OTQ3OH0.MtFsOH9tAbGGO8rs3R43fMed55zT-v_WuKOjPBremrA"

val httpClient = OkHttpClient()

suspend fun uploadShiftAndPdfToSupabase(shift: CompletedShift, pdfBytes: ByteArray): String? {
    return withContext(Dispatchers.IO) {
        try {
            val safeName = shift.patient.name
                .replace(" ", "_")
                .replace("á", "a").replace("é", "e").replace("í", "i")
                .replace("ó", "o").replace("ö", "o").replace("ő", "o")
                .replace("ú", "u").replace("ü", "u").replace("ű", "u")
            val fileName = "Bizonylat_${safeName}_${System.currentTimeMillis()}.pdf"

            val storageUrl = "$SUPABASE_URL/storage/v1/object/receipts/$fileName"
            val pdfRequestBody = pdfBytes.toRequestBody("application/pdf".toMediaType())

            val storageRequest = Request.Builder()
                .url(storageUrl)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer $SUPABASE_KEY")
                .post(pdfRequestBody)
                .build()

            val storageResponse = httpClient.newCall(storageRequest).execute()
            if (!storageResponse.isSuccessful) {
                println("Storage hiba: ${storageResponse.body?.string()}")
                return@withContext null
            }

            val publicPdfUrl = "$SUPABASE_URL/storage/v1/object/public/receipts/$fileName"

            val jsonObject = JSONObject().apply {
                put("patient_id", shift.patient.id)
                put("patient_name", shift.patient.name)
                put("patient_address", shift.patient.address)
                put("patient_email", shift.patient.email)
                put("shift_date", shift.date)
                put("start_time", shift.startTime)
                put("end_time", shift.endTime)
                put("nurse_wage", shift.nurseWage)
                put("patient_cost", shift.patientCost)
                put("receipt_pdf_url", publicPdfUrl)
            }

            val dbUrl = "$SUPABASE_URL/rest/v1/completed_shifts"
            val dbRequestBody = jsonObject.toString().toRequestBody("application/json".toMediaType())

            val dbRequest = Request.Builder()
                .url(dbUrl)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer $SUPABASE_KEY")
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .post(dbRequestBody)
                .build()

            val dbResponse = httpClient.newCall(dbRequest).execute()
            if (!dbResponse.isSuccessful) {
                println("DB hiba: ${dbResponse.body?.string()}")
                return@withContext null
            }

            publicPdfUrl
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
suspend fun fetchAllDataFromSupabase() {
    withContext(Dispatchers.IO) {
        try {
            val patientsRequest = Request.Builder()
                .url("$SUPABASE_URL/rest/v1/patients?select=*&order=scheduled_time.asc")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer $SUPABASE_KEY")
                .get()
                .build()

            val patientsResponse = httpClient.newCall(patientsRequest).execute()
            if (patientsResponse.isSuccessful) {
                val jsonString = patientsResponse.body?.string() ?: "[]"
                val jsonArray = org.json.JSONArray(jsonString)
                val loadedPatients = mutableListOf<Patient>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    loadedPatients.add(
                        Patient(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            address = obj.getString("address"),
                            scheduledTime = obj.getString("scheduled_time"),
                            phone = obj.getString("phone"),
                            email = obj.getString("email"),
                            description = obj.getString("description")
                        )
                    )
                }
                withContext(Dispatchers.Main) {
                    activePatients.clear()
                    activePatients.addAll(loadedPatients)
                }
            }

            val shiftsRequest = Request.Builder()
                .url("$SUPABASE_URL/rest/v1/completed_shifts?select=*&order=created_at.desc")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer $SUPABASE_KEY")
                .get()
                .build()

            val shiftsResponse = httpClient.newCall(shiftsRequest).execute()
            if (shiftsResponse.isSuccessful) {
                val jsonString = shiftsResponse.body?.string() ?: "[]"
                val jsonArray = org.json.JSONArray(jsonString)
                val loadedShifts = mutableListOf<CompletedShift>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    loadedShifts.add(
                        CompletedShift(
                            patient = Patient(
                                id = obj.getString("patient_id"),
                                name = obj.getString("patient_name"),
                                address = obj.getString("patient_address"),
                                scheduledTime = "",
                                email = obj.getString("patient_email")
                            ),
                            date = obj.getString("shift_date"),
                            startTime = obj.getString("start_time"),
                            endTime = obj.getString("end_time"),
                            nurseWage = obj.getInt("nurse_wage"),
                            patientCost = obj.getInt("patient_cost"),
                            receiptPdfUrl = obj.getString("receipt_pdf_url")
                        )
                    )
                }
                withContext(Dispatchers.Main) {
                    completedShifts.clear()
                    completedShifts.addAll(loadedShifts)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

suspend fun removePatientFromActiveDb(patientId: String) {
    withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$SUPABASE_URL/rest/v1/patients?id=eq.$patientId")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer $SUPABASE_KEY")
                .delete()
                .build()
            httpClient.newCall(request).execute()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}