package com.example.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class CurrencyVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val formattedText = formatCurrency(originalText)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // Approximate mapping: for every 3 digits, we added a dot
                var dotsCount = 0
                for (i in 0 until minOf(offset, originalText.length)) {
                    val reversedIndex = originalText.length - 1 - i
                    if (reversedIndex > 0 && reversedIndex % 3 == 0) {
                        dotsCount++
                    }
                }
                
                // Let's do a simpler mapping for end of string
                if (offset == originalText.length) {
                    return formattedText.length
                }
                
                // For simplicity in a basic implementation, we just return the corresponding offset
                // taking into account the added dots
                val digitsBefore = offset
                val dotsBefore = (digitsBefore - 1) / 3
                return offset + dotsBefore
            }

            override fun transformedToOriginal(offset: Int): Int {
                // Simpler mapping back
                if (offset == formattedText.length) {
                    return originalText.length
                }
                var originalOffset = 0
                for (i in 0 until offset) {
                    if (formattedText[i].isDigit()) {
                        originalOffset++
                    }
                }
                return originalOffset
            }
        }

        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }

    private fun formatCurrency(text: String): String {
        return try {
            val symbols = DecimalFormatSymbols(Locale("vi", "VN"))
            symbols.groupingSeparator = '.'
            val format = DecimalFormat("#,###", symbols)
            format.format(text.toLong())
        } catch (e: Exception) {
            text
        }
    }
}
