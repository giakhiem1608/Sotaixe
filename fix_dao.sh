sed -i '46,54c\
    @Query("SELECT COUNT(*) FROM revenue_entries WHERE sourceId = :sourceId")\
    suspend fun countRevenueEntries(sourceId: Int): Int\
\
    @Delete\
    suspend fun deleteRevenueSource(source: RevenueSource)\
\
    @Query("DELETE FROM revenue_entries WHERE id = :id")\
    suspend fun deleteRevenueEntry(id: Int)
' app/src/main/java/com/example/data/LedgerDao.kt

sed -i '69,76c\
    @Query("SELECT COUNT(*) FROM expense_entries WHERE categoryId = :categoryId")\
    suspend fun countExpenseEntries(categoryId: Int): Int\
\
    @Delete\
    suspend fun deleteExpenseCategory(category: ExpenseCategory)\
\
    @Query("DELETE FROM expense_entries WHERE id = :id")\
    suspend fun deleteExpenseEntry(id: Int)
' app/src/main/java/com/example/data/LedgerDao.kt
