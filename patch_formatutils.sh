sed -i '/fun formatCurrency/i \
    fun formatTime(timestamp: Long): String {\n        val sdf = SimpleDateFormat("HH:mm", Locale("vi", "VN"))\n        return sdf.format(Date(timestamp))\n    }' app/src/main/java/com/example/utils/FormatUtils.kt
