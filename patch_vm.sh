sed -i '/fun saveGoal/i \    fun deleteGoal(goal: Goal) {\n        viewModelScope.launch { repository.deleteGoal(goal) }\n    }' app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt
