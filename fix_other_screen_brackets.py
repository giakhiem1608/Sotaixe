import re

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "r") as f:
    content = f.read()

# Let's replace the messed up braces and branding.
pattern = re.compile(r'                SettingsMenuItem\(\n                    title = "Xóa toàn bộ dữ liệu",\n                    subtitle = "Đặt lại ứng dụng như ban đầu",\n                    icon = Icons\.Default\.DeleteForever,\n                    onClick = \{ showResetConfirmDialog = true \},\n                    isDestructive = true\n                \)\n            \}\n        \}(.*?)\n    if \(showResetConfirmDialog\)', re.DOTALL)

def replacer(match):
    return """                SettingsMenuItem(
                    title = "Xóa toàn bộ dữ liệu",
                    subtitle = "Đặt lại ứng dụng như ban đầu",
                    icon = Icons.Default.DeleteForever,
                    onClick = { showResetConfirmDialog = true },
                    isDestructive = true
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(16.dp))
        
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🚗 Made for drivers • ", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("LHN", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("  ·  v$APP_VERSION", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
    
    if (showResetConfirmDialog)"""

content = pattern.sub(replacer, content)

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "w") as f:
    f.write(content)
