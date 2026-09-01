sed -i 's/var showXlsxDialog by remember { mutableStateOf(false) }/var showExportDialog by remember { mutableStateOf(false) }/g' app/src/main/java/com/example/ui/screens/OtherScreen.kt
sed -i 's/showXlsxDialog = true/showExportDialog = true/g' app/src/main/java/com/example/ui/screens/OtherScreen.kt
sed -i '/if (showXlsxDialog) {/,/        }/d' app/src/main/java/com/example/ui/screens/OtherScreen.kt
sed -i '/if (showExportDialog) {/d' app/src/main/java/com/example/ui/screens/OtherScreen.kt
sed -i '/if (showManageSources) {/i \    if (showExportDialog) {\n        ExportBottomSheet(viewModel, { showExportDialog = false }, context)\n    }' app/src/main/java/com/example/ui/screens/OtherScreen.kt
