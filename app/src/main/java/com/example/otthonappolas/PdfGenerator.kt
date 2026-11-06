package com.example.otthonappolas

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

fun generateReceiptPdfBytes(shift: CompletedShift): ByteArray {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    val titlePaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 24f
        color = Color.BLACK
    }
    val headerPaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 16f
        color = Color.DKGRAY
    }
    val bodyPaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textSize = 14f
        color = Color.BLACK
    }
    val linePaint = Paint().apply {
        color = Color.LTGRAY
        strokeWidth = 2f
    }

    var yPos = 60f
    canvas.drawText("OtthonAPPolás - Kezelési Bizonylat", 50f, yPos, titlePaint)
    yPos += 25f
    canvas.drawLine(50f, yPos, 545f, yPos, linePaint)

    yPos += 40f
    canvas.drawText("Páciens adatai", 50f, yPos, headerPaint)
    yPos += 25f
    canvas.drawText("Név: ${shift.patient.name}", 50f, yPos, bodyPaint)
    yPos += 20f
    canvas.drawText("Cím: ${shift.patient.address}", 50f, yPos, bodyPaint)
    yPos += 20f
    canvas.drawText("Értesítési email: ${shift.patient.email}", 50f, yPos, bodyPaint)

    yPos += 40f
    canvas.drawText("Elvégzett kezelés részletei", 50f, yPos, headerPaint)
    yPos += 25f
    canvas.drawText("Dátum: ${shift.date}", 50f, yPos, bodyPaint)
    yPos += 20f
    canvas.drawText("Időtartam: ${shift.startTime} - ${shift.endTime}", 50f, yPos, bodyPaint)
    yPos += 20f
    canvas.drawText("Leírás: ${shift.patient.description}", 50f, yPos, bodyPaint)

    yPos += 40f
    canvas.drawLine(50f, yPos, 545f, yPos, linePaint)
    yPos += 30f
    canvas.drawText("Kezelési költség: ${shift.patientCost} Ft", 50f, yPos, titlePaint)

    yPos += 60f
    canvas.drawText("Hitelesítés: A páciens digitális aláírásával rögzítve.", 50f, yPos, bodyPaint)

    pdfDocument.finishPage(page)

    val outputStream = ByteArrayOutputStream()
    pdfDocument.writeTo(outputStream)
    pdfDocument.close()

    return outputStream.toByteArray()
}

fun sendReceiptEmail(context: Context, shift: CompletedShift) {
    try {
        val pdfBytes = shift.pdfBytes ?: generateReceiptPdfBytes(shift)
        val cleanName = shift.patient.name.replace(" ", "_")
        val fileName = "Bizonylat_${cleanName}_${shift.date}.pdf"

        val cacheFile = File(context.cacheDir, fileName)
        FileOutputStream(cacheFile).use { it.write(pdfBytes) }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            cacheFile
        )

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(shift.patient.email))
            putExtra(Intent.EXTRA_SUBJECT, "OtthonAPPolás - Kezelési bizonylat (${shift.date})")
            putExtra(
                Intent.EXTRA_TEXT,
                "Tisztelt ${shift.patient.name} / Hozzátartozó!\n\n" +
                        "Mellékelten küldjük a mai (${shift.date}, ${shift.startTime}-${shift.endTime}) otthonápolási kezelés hivatalos PDF bizonylatát.\n" +
                        "A kezelés rögzített díja: ${shift.patientCost} Ft.\n\n" +
                        "Üdvözlettel,\nOtthonAPPolás Szeged"
            )
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(emailIntent, "Bizonylat küldése a hozzátartozónak..."))
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Hiba az email előkészítésekor: ${e.message}", Toast.LENGTH_LONG).show()
    }
}