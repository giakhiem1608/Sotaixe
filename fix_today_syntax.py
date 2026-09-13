import re

with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "r") as f:
    content = f.read()

content = content.replace("val tipColor = if (tipColorHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(tipColorHex)) } catch (e: Exception) { Color(0xFFF59E0B) } else Color(0xFFF59E0B) } else ExpenseError", "val tipColor = if (tipColorHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(tipColorHex)) } catch (e: Exception) { Color(0xFFF59E0B) } else Color(0xFFF59E0B)")

with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "w") as f:
    f.write(content)

