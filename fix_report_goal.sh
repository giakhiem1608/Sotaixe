sed -i 's/viewModel.setGoal(type, amount)/viewModel.saveGoal(type, amount)/g' app/src/main/java/com/example/ui/screens/ReportScreen.kt
sed -i 's/viewModel.deleteGoal()/if (goal != null) { viewModel.deleteGoal(goal!!) }/g' app/src/main/java/com/example/ui/screens/ReportScreen.kt
