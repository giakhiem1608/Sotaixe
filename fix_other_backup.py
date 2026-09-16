import re

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "r") as f:
    content = f.read()

old_backup = """    val createBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            coroutineScope.launch {
                val success = viewModel.exportBackupToFile(context, it)
                if (success) {
                    Toast.makeText(context, "Sao lưu dữ liệu thành công!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Lỗi khi sao lưu dữ liệu", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Launcher for file picking (restore)
    val restoreBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            coroutineScope.launch {
                val success = viewModel.importBackupFromFile(context, it)
                if (success) {
                    Toast.makeText(context, "Khôi phục dữ liệu thành công!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Lỗi khi khôi phục hoặc file không hợp lệ", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }"""

new_backup = """    val createBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            viewModel.generateBackupData { json ->
                if (json != null) {
                    try {
                        context.contentResolver.openOutputStream(it)?.use { stream ->
                            stream.write(json.toByteArray())
                        }
                        Toast.makeText(context, "Sao lưu dữ liệu thành công!", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Lỗi khi lưu file", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Lỗi tạo dữ liệu sao lưu", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val restoreBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    val json = stream.bufferedReader().use { reader -> reader.readText() }
                    viewModel.restoreBackupData(json) { success ->
                        if (success) {
                            Toast.makeText(context, "Khôi phục dữ liệu thành công!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Lỗi dữ liệu", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi đọc file", Toast.LENGTH_SHORT).show()
            }
        }
    }"""

content = content.replace(old_backup, new_backup)

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "w") as f:
    f.write(content)

