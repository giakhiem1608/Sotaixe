sed -i '/suspend fun insertGoal/a \    suspend fun deleteGoal(goal: Goal) { dao.deleteGoal(goal) }' app/src/main/java/com/example/data/LedgerRepository.kt
