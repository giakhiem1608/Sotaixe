package com.example.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatUtils {
    private val localeVN = Locale("vi", "VN")
    private val currencyFormat = NumberFormat.getCurrencyInstance(localeVN)
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", localeVN)
    private val dbDateFormat = SimpleDateFormat("yyyy-MM-dd", localeVN)
    private val monthFormat = SimpleDateFormat("MM/yyyy", localeVN)
    private val dbMonthFormat = SimpleDateFormat("yyyy-MM", localeVN)
    private val dayOfWeekFormat = SimpleDateFormat("EEEE", localeVN)

    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale("vi", "VN"))
        return sdf.format(Date(timestamp))
    }
    fun formatCurrency(amount: Long): String {
        return currencyFormat.format(amount).replace("₫", "đ").replace(" ", "").replace(",00", "")
    }

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }
    
    fun formatDbDate(timestamp: Long): String {
        return dbDateFormat.format(Date(timestamp))
    }
    
    fun getDayOfWeek(timestamp: Long): String {
        return dayOfWeekFormat.format(Date(timestamp))
    }
    
    fun parseDbDate(dateString: String): Long {
        return dbDateFormat.parse(dateString)?.time ?: 0L
    }
    
    fun parseDbMonth(monthString: String): java.util.Date? {
        return dbMonthFormat.parse(monthString)
    }
    
    fun formatMonth(timestamp: Long): String {
        return monthFormat.format(Date(timestamp))
    }
    
    fun formatDbMonth(timestamp: Long): String {
        return dbMonthFormat.format(Date(timestamp))
    }
}
