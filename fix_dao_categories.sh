sed -i '26,29c\
    @Query("SELECT * FROM expense_categories")\
    fun getAllExpenseCategoriesFlow(): Flow<List<ExpenseCategory>>\
\
    @Query("SELECT * FROM expense_categories WHERE isActive = 1 ORDER BY isDefault DESC, name ASC")\
    fun getActiveExpenseCategories(): Flow<List<ExpenseCategory>>
' app/src/main/java/com/example/data/LedgerDao.kt
