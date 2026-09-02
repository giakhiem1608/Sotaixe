import re

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "r") as f:
    content = f.read()

# Let's clean up the mess.
# First, remove the incorrectly placed branding completely.
branding_pattern = re.compile(r'        Spacer\(modifier = Modifier\.height\(32\.dp\)\)\n\s*Column\(\n\s*modifier = Modifier\.fillMaxWidth\(\),\n\s*horizontalAlignment = Alignment\.CenterHorizontally\n\s*\) \{\n\s*Row\(verticalAlignment = Alignment\.CenterVertically\) \{\n\s*Text\("🚗 Made for drivers • ".*?\n\s*Text\("LHN".*?\n\s*Text\("  ·  v\$APP_VERSION".*?\n\s*\}\n\s*\}\n\s*Spacer\(modifier = Modifier\.height\(24\.dp\)\)\n\s*\}', re.DOTALL)

content = branding_pattern.sub(r'            }\n        }\n    }', content)

# Now we need to insert the branding at the correct place.
# The correct place is at the end of the Scaffold's Column.
# The Scaffold's Column ends after the "Xóa toàn bộ dữ liệu" Card.
# Let's find:
target_end = """        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                SettingsMenuItem(
                    title = "Xóa toàn bộ dữ liệu",
                    subtitle = "Đặt lại ứng dụng như ban đầu",
                    icon = Icons.Default.DeleteForever,
                    onClick = { showResetConfirmDialog = true },
                    isDestructive = true
                )
            }
        }
    }"""

branding_insert = """        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                SettingsMenuItem(
                    title = "Xóa toàn bộ dữ liệu",
                    subtitle = "Đặt lại ứng dụng như ban đầu",
                    icon = Icons.Default.DeleteForever,
                    onClick = { showResetConfirmDialog = true },
                    isDestructive = true
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f)) // Push to bottom if content is short
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
    }"""

content = content.replace(target_end, branding_insert)

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "w") as f:
    f.write(content)
