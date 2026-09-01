sed -i 's/repository.insertGoal(/val currentId = currentMonthGoal.value?.id ?: 0\n            repository.insertGoal(/g' app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt
sed -i 's/type = type,/id = currentId,\n                    type = type,/g' app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt
