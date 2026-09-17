with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "r") as f:
    content = f.read()
import re
content = re.sub(r'@OptIn\(ExperimentalMaterial3Api::class\)\s*@Composable\s*@OptIn\(androidx.compose.material3.ExperimentalMaterial3Api::class\)\s*@Composable', '@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)\n@Composable', content)
with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "w") as f:
    f.write(content)
