import re

with open("app/src/main/java/com/example/data/LedgerDao.kt", "r") as f:
    content = f.read()

# Add flow versions
content = content.replace(
    'fun getRevenueEntriesByMonth(monthPrefix: String): Flow<List<RevenueEntry>>',
    'fun getRevenueEntriesByMonth(monthPrefix: String): Flow<List<RevenueEntry>>\n\n    @Query("SELECT * FROM revenue_entries ORDER BY timestamp DESC")\n    fun getAllRevenueEntriesFlow(): Flow<List<RevenueEntry>>'
)

content = content.replace(
    'fun getExpenseEntriesByMonth(monthPrefix: String): Flow<List<ExpenseEntry>>',
    'fun getExpenseEntriesByMonth(monthPrefix: String): Flow<List<ExpenseEntry>>\n\n    @Query("SELECT * FROM expense_entries ORDER BY timestamp DESC")\n    fun getAllExpenseEntriesFlow(): Flow<List<ExpenseEntry>>'
)

with open("app/src/main/java/com/example/data/LedgerDao.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/data/LedgerRepository.kt", "r") as f:
    repo = f.read()

repo = repo.replace(
    'fun getRevenueEntriesByMonth(monthPrefix: String): Flow<List<RevenueEntry>> {\n        return dao.getRevenueEntriesByMonth(monthPrefix)\n    }',
    'fun getRevenueEntriesByMonth(monthPrefix: String): Flow<List<RevenueEntry>> {\n        return dao.getRevenueEntriesByMonth(monthPrefix)\n    }\n\n    fun getAllRevenueEntriesFlow(): Flow<List<RevenueEntry>> = dao.getAllRevenueEntriesFlow()'
)

repo = repo.replace(
    'fun getExpenseEntriesByMonth(monthPrefix: String): Flow<List<ExpenseEntry>> {\n        return dao.getExpenseEntriesByMonth(monthPrefix)\n    }',
    'fun getExpenseEntriesByMonth(monthPrefix: String): Flow<List<ExpenseEntry>> {\n        return dao.getExpenseEntriesByMonth(monthPrefix)\n    }\n\n    fun getAllExpenseEntriesFlow(): Flow<List<ExpenseEntry>> = dao.getAllExpenseEntriesFlow()'
)

with open("app/src/main/java/com/example/data/LedgerRepository.kt", "w") as f:
    f.write(repo)
print("Updated Dao and Repo")
