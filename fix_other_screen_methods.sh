sed -i 's/viewModel.backupData/viewModel.generateBackupData/g' app/src/main/java/com/example/ui/screens/OtherScreen.kt
sed -i 's/viewModel.restoreData/viewModel.restoreBackupData/g' app/src/main/java/com/example/ui/screens/OtherScreen.kt
sed -i 's/viewModel.resetAllData()/coroutineScope.launch { viewModel.resetAllData() }/g' app/src/main/java/com/example/ui/screens/OtherScreen.kt
