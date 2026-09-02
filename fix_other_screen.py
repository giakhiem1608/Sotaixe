import re

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "r") as f:
    content = f.read()

# I will add showThemeSettings state
content = content.replace(
    'var showManageCategories by remember { mutableStateOf(false) }',
    'var showManageCategories by remember { mutableStateOf(false) }\n    var showThemeSettings by remember { mutableStateOf(false) }'
)

# Add GIAO DIỆN group after QUẢN LÝ
giao_dien_group = """        Text("GIAO DIỆN", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                SettingsMenuItem(
                    title = "Card Thu nhập",
                    subtitle = "Tùy chỉnh màu sắc Thu nhập hôm nay",
                    icon = Icons.Default.Palette,
                    onClick = { showThemeSettings = true }
                )
            }
        }
        
        Text("DỮ LIỆU","""

content = content.replace('Text("DỮ LIỆU",', giao_dien_group)

# Add if (showThemeSettings)
theme_settings_modal = """
    if (showThemeSettings) {
        ThemeSettingsSheet(viewModel = viewModel, onDismiss = { showThemeSettings = false })
    }
"""

content = content.replace('if (showManageCategories) {', theme_settings_modal + '\n    if (showManageCategories) {')

# Add import for Icons.Default.Palette
content = content.replace('import androidx.compose.material.icons.filled.DeleteForever', 'import androidx.compose.material.icons.filled.DeleteForever\nimport androidx.compose.material.icons.filled.Palette')

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "w") as f:
    f.write(content)
