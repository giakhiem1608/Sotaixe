import re
import os

files = [
    "app/src/main/java/com/example/ui/screens/HistoryScreen.kt",
    "app/src/main/java/com/example/ui/screens/OtherScreen.kt",
    "app/src/main/java/com/example/ui/screens/ReportScreen.kt",
    "app/src/main/java/com/example/ui/screens/TodayScreen.kt"
]

# Keep in TodayScreen, remove from others
for fpath in files:
    with open(fpath, "r") as f:
        content = f.read()
    
    # Remove the color definitions
    if fpath != "app/src/main/java/com/example/ui/screens/TodayScreen.kt":
        content = content.replace("val BgColor = Color(0xFFF8FAFC)\nval CardSurface = Color(0xFFFFFFFF)\nval CardBorder = Color(0xFFE2E8F0)\n", "")
        # Also maybe the python script added them multiple times if I ran it twice. Let's do regex removal.
        content = re.sub(r'val BgColor = Color\(0xFFF8FAFC\)\n?', '', content)
        content = re.sub(r'val CardSurface = Color\(0xFFFFFFFF\)\n?', '', content)
        content = re.sub(r'val CardBorder = Color\(0xFFE2E8F0\)\n?', '', content)
        
    with open(fpath, "w") as f:
        f.write(content)

# Fix luminance in ThemeSettingsScreen
ts_path = "app/src/main/java/com/example/ui/screens/ThemeSettingsScreen.kt"
with open(ts_path, "r") as f:
    ts = f.read()
    ts = ts.replace("androidx.compose.ui.graphics.luminance(color)", "color.luminance()")
with open(ts_path, "w") as f:
    f.write(ts)

