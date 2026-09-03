import re

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "r") as f:
    content = f.read()

content = content.replace("🚗 Made for drivers • ", "BA BON • ")
content = content.replace("LHN", "Version 1.0")
content = content.replace("  ·  v$APP_VERSION", "")

# Also add the "Thông tin phần mềm" menu item
target = """        Text("HỆ THỐNG", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
        Card(
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
        }"""

replacement = """        Text("THÔNG TIN", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                SettingsMenuItem(
                    title = "Thông tin phần mềm",
                    subtitle = "Phiên bản, nhà phát triển",
                    icon = Icons.Default.Info,
                    onClick = { showAppInfoSheet = true }
                )
            }
        }

        Text("HỆ THỐNG", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
        Card(
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
        }"""

content = content.replace(target, replacement)

# Add showAppInfoSheet variable
content = content.replace("var showResetConfirmDialog2 by remember { mutableStateOf(false) }", "var showResetConfirmDialog2 by remember { mutableStateOf(false) }\n    var showAppInfoSheet by remember { mutableStateOf(false) }")

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "w") as f:
    f.write(content)

# We also need to add AppInfoSheet component. We will append it to the file.
app_info_sheet = """
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Sổ Tài Xế", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Version 1.0", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Phát triển bởi BA BON", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(32.dp))
            Text("© 2026 BA BON", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("ĐÓNG")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
"""

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "a") as f:
    f.write(app_info_sheet)

