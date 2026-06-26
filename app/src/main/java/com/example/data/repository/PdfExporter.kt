package com.example.data.repository

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.data.model.ExpenseEntity
import com.example.data.model.GroupEntity
import com.example.data.model.MemberEntity
import com.example.domain.SimplifiedDebt
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {
    fun exportExpensesToPdf(
        context: Context,
        group: GroupEntity,
        members: List<MemberEntity>,
        expenses: List<ExpenseEntity>,
        debts: List<SimplifiedDebt>
    ): File? {
        val pdfDocument = PdfDocument()
        
        // A4 page: 595 x 842 points
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        
        val paint = Paint()
        val titlePaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 20f
            color = Color.BLACK
        }
        val headerPaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 14f
            color = Color.DKGRAY
        }
        val boldPaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 11f
            color = Color.BLACK
        }
        val textPaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textSize = 11f
            color = Color.BLACK
        }
        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }

        var yPosition = 50f
        
        // SettleX Brand Top Line (Premium Fintech Green #00E676)
        val brandColor = Color.parseColor("#00E676")
        canvas.drawRect(30f, yPosition, 565f, yPosition + 4f, Paint().apply { color = brandColor })
        yPosition += 25f
        
        // Document Title
        canvas.drawText("SettleX Group Statement", 30f, yPosition, titlePaint)
        
        val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        canvas.drawText("Generated on: $dateStr", 30f, yPosition + 15f, Paint().apply {
            textSize = 9f
            color = Color.GRAY
        })
        yPosition += 45f

        // Group info
        canvas.drawText("Group Details", 30f, yPosition, headerPaint)
        yPosition += 18f
        canvas.drawText("Group Name: ${group.name}", 30f, yPosition, boldPaint)
        yPosition += 15f
        canvas.drawText("Description: ${group.description}", 30f, yPosition, textPaint)
        yPosition += 25f
        
        val membersStr = members.joinToString { it.name }
        canvas.drawText("Members: $membersStr", 30f, yPosition, textPaint)
        yPosition += 35f

        canvas.drawLine(30f, yPosition, 565f, yPosition, linePaint)
        yPosition += 20f

        // Expenses Table
        canvas.drawText("Logged Expenses", 30f, yPosition, headerPaint)
        yPosition += 20f

        canvas.drawText("Title", 30f, yPosition, boldPaint)
        canvas.drawText("Paid By", 220f, yPosition, boldPaint)
        canvas.drawText("Category", 360f, yPosition, boldPaint)
        canvas.drawText("Amount", 480f, yPosition, boldPaint)
        yPosition += 8f
        canvas.drawLine(30f, yPosition, 565f, yPosition, linePaint)
        yPosition += 18f

        var totalAmount = 0.0
        for (expense in expenses) {
            if (yPosition > 650f) {
                break
            }
            
            val paidBy = members.find { it.id == expense.paidByMemberId }?.name ?: "Someone"
            
            canvas.drawText(expense.title.take(28), 30f, yPosition, textPaint)
            canvas.drawText(paidBy.take(20), 220f, yPosition, textPaint)
            canvas.drawText(expense.category, 360f, yPosition, textPaint)
            canvas.drawText("₹${String.format("%.2f", expense.amount)}", 480f, yPosition, textPaint)
            totalAmount += expense.amount
            yPosition += 18f
        }
        
        yPosition += 10f
        canvas.drawLine(30f, yPosition, 565f, yPosition, linePaint)
        yPosition += 18f
        canvas.drawText("Total Group Spending:", 30f, yPosition, boldPaint)
        canvas.drawText("₹${String.format("%.2f", totalAmount)}", 480f, yPosition, boldPaint)
        yPosition += 35f

        // Debt Simplification Results
        canvas.drawLine(30f, yPosition, 565f, yPosition, linePaint)
        yPosition += 20f
        canvas.drawText("Settlement Calculations (Greedy Debt Simplifier)", 30f, yPosition, headerPaint)
        yPosition += 20f

        if (debts.isEmpty()) {
            canvas.drawText("All debts are settled! No payments are pending.", 30f, yPosition, textPaint)
        } else {
            for (debt in debts) {
                if (yPosition > 800f) break
                val debtText = "${debt.fromMemberName} owes ${debt.toMemberName}  ->  ₹${String.format("%.2f", debt.amount)}"
                canvas.drawText(debtText, 30f, yPosition, textPaint)
                yPosition += 18f
            }
        }

        pdfDocument.finishPage(page)
        
        // Write out PDF to the app's cache directory (for easy file sharing)
        val file = File(context.cacheDir, "SettleX_Statement_${group.name.replace(" ", "_")}.pdf")
        return try {
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.close()
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }
}
