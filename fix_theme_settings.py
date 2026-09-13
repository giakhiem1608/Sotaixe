import re

with open("app/src/main/java/com/example/ui/screens/ThemeSettingsScreen.kt", "r") as f:
    content = f.read()

content = content.replace("fun ThemeSettingsScreen(", "fun ThemeSettingsSheet(")
content = content.replace("themeManager.updateColors(", "themeManager.setCardColors(")
content = content.replace("ThemeColorPicker(", "ThemeColorPickerSheet(")

with open("app/src/main/java/com/example/ui/screens/ThemeSettingsScreen.kt", "w") as f:
    f.write(content)
