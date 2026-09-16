import re

def fix_file(filename):
    with open(filename, "r") as f:
        content = f.read()

    # Remove misplaced imports at the very beginning
    while content.startswith("import"):
        lines = content.split('\n')
        content = '\n'.join(lines[1:])

    # Find the package declaration
    pkg_match = re.search(r'^package .*\n', content, re.MULTILINE)
    if pkg_match:
        pkg_str = pkg_match.group(0)
        # Remove it from current position
        content = content.replace(pkg_str, '', 1)
        # Put it at the very top
        content = pkg_str + "\n" + content

    # Define common colors if not imported
    if "val BgColor" not in content and "import com.example.ui.screens.BgColor" not in content:
        color_defs = "\nval BgColor = Color(0xFFF8FAFC)\nval CardSurface = Color(0xFFFFFFFF)\nval CardBorder = Color(0xFFE2E8F0)\n"
        # Insert after imports
        last_import = list(re.finditer(r'^import .*\n', content, re.MULTILINE))[-1]
        insert_pos = last_import.end()
        content = content[:insert_pos] + color_defs + content[insert_pos:]

    if "HistoryScreen.kt" in filename:
        if "import androidx.compose.foundation.verticalScroll" not in content:
            content = content.replace("import androidx.compose.foundation.lazy.LazyColumn", "import androidx.compose.foundation.verticalScroll\nimport androidx.compose.foundation.rememberScrollState\nimport androidx.compose.foundation.lazy.LazyColumn")

    if "OtherScreen.kt" in filename:
        if "import androidx.compose.foundation.shape.CircleShape" not in content:
            content = content.replace("import androidx.compose.foundation.shape.RoundedCornerShape", "import androidx.compose.foundation.shape.CircleShape\nimport androidx.compose.foundation.shape.RoundedCornerShape")
        
        # ThemeSettingsSheet import isn't needed if in same package, but the compiler said "Unresolved reference ThemeSettingsSheet". Let's check ThemeSettingsScreen.kt package. It's in the same package. Maybe it failed to compile because of luminance error?

    with open(filename, "w") as f:
        f.write(content)

fix_file("app/src/main/java/com/example/ui/screens/HistoryScreen.kt")
fix_file("app/src/main/java/com/example/ui/screens/OtherScreen.kt")
fix_file("app/src/main/java/com/example/ui/screens/ReportScreen.kt")

# Fix luminance in ThemeSettingsScreen
with open("app/src/main/java/com/example/ui/screens/ThemeSettingsScreen.kt", "r") as f:
    ts = f.read()
    if "import androidx.compose.ui.graphics.luminance" not in ts:
        ts = ts.replace("import androidx.compose.ui.graphics.Color", "import androidx.compose.ui.graphics.Color\nimport androidx.compose.ui.graphics.luminance")
with open("app/src/main/java/com/example/ui/screens/ThemeSettingsScreen.kt", "w") as f:
    f.write(ts)

