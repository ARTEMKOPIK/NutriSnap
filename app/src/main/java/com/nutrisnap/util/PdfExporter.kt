package com.nutrisnap.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.nutrisnap.data.local.FoodEntry
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfExporter(private val context: Context) {
    fun exportToPdf(entries: List<FoodEntry>): File? {
        val pdfDocument = PdfDocument()
        val titlePaint =
            Paint().apply {
                textSize = 24f
                isFakeBoldText = true
            }
        val bodyPaint =
            Paint().apply {
                textSize = 16f
            }

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        canvas.drawText("Отчет о питании NutriSnap", 50f, 50f, titlePaint)

        var yPosition = 100f
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

        entries.forEach { entry ->
            if (yPosition > 800f) {
                // Should start a new page here in a real implementation
            }
            val dateStr = sdf.format(Date(entry.timestamp))
            canvas.drawText("$dateStr: ${entry.dishName}", 50f, yPosition, bodyPaint)
            yPosition += 25f
            canvas.drawText(
                "Калории: ${entry.calories} ккал (Б: ${entry.proteins}г, Ж: ${entry.fats}г, У: ${entry.carbs}г)",
                70f,
                yPosition,
                bodyPaint,
            )
            yPosition += 40f
        }

        pdfDocument.finishPage(page)

        val file = File(context.getExternalFilesDir(null), "NutriSnap_Report_${System.currentTimeMillis()}.pdf")
        return try {
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            file
        } catch (e: Exception) {
            pdfDocument.close()
            null
        }
    }
}
