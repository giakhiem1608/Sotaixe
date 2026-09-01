sed -i 's/showCsvDialog = true/showXlsxDialog = true/g' app/src/main/java/com/example/ui/screens/OtherScreen.kt
sed -i 's/showCsvDialog/showXlsxDialog/g' app/src/main/java/com/example/ui/screens/OtherScreen.kt
sed -i 's/"Xuất dữ liệu"/"Xuất dữ liệu"/g' app/src/main/java/com/example/ui/screens/OtherScreen.kt
sed -i 's/"Lưu dữ liệu ra file Excel (CSV)"/"Lưu dữ liệu ra file Excel (.xlsx)"/g' app/src/main/java/com/example/ui/screens/OtherScreen.kt
sed -i 's/val csvLauncher =/val xlsxLauncher =/g' app/src/main/java/com/example/ui/screens/OtherScreen.kt
sed -i 's/"text\/csv"/"application\/vnd.openxmlformats-officedocument.spreadsheetml.sheet"/g' app/src/main/java/com/example/ui/screens/OtherScreen.kt
sed -i 's/viewModel.exportCsvData(currentMonth) { csvData ->/viewModel.exportXlsxData(currentMonth, context.contentResolver.openOutputStream(it)!!) { success ->/g' app/src/main/java/com/example/ui/screens/OtherScreen.kt
