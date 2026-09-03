import re

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "r") as f:
    content = f.read()

if "import androidx.compose.material.icons.filled.WarningAmber" not in content:
    content = content.replace("import androidx.compose.material.icons.filled.Edit", "import androidx.compose.material.icons.filled.Edit\nimport androidx.compose.material.icons.filled.WarningAmber")

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "w") as f:
    f.write(content)

