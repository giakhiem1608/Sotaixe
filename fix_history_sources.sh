sed -i '13a\
    @Query("SELECT * FROM revenue_sources")\
    fun getAllRevenueSourcesFlow(): Flow<List<RevenueSource>>
' app/src/main/java/com/example/data/LedgerDao.kt

sed -i '26a\
    @Query("SELECT * FROM expense_categories")\
    fun getAllExpenseCategoriesFlow(): Flow<List<ExpenseCategory>>
' app/src/main/java/com/example/data/LedgerDao.kt

sed -i '11a\
    fun getAllRevenueSourcesFlow() = dao.getAllRevenueSourcesFlow()\
    fun getAllExpenseCategoriesFlow() = dao.getAllExpenseCategoriesFlow()\
' app/src/main/java/com/example/data/LedgerRepository.kt

sed -i '34a\
    val allRevenueSources: StateFlow<List<RevenueSource>> = repository.getAllRevenueSourcesFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())\
    val allExpenseCategories: StateFlow<List<ExpenseCategory>> = repository.getAllExpenseCategoriesFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())\
' app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt

sed -i 's/val sources by viewModel.activeRevenueSources.collectAsState()/val sources by viewModel.allRevenueSources.collectAsState()/g' app/src/main/java/com/example/ui/screens/HistoryScreen.kt
sed -i 's/val categories by viewModel.activeExpenseCategories.collectAsState()/val categories by viewModel.allExpenseCategories.collectAsState()/g' app/src/main/java/com/example/ui/screens/HistoryScreen.kt

sed -i 's/val sources by viewModel.activeRevenueSources.collectAsState()/val sources by viewModel.allRevenueSources.collectAsState()/g' app/src/main/java/com/example/ui/screens/ReportScreen.kt
sed -i 's/val categories by viewModel.activeExpenseCategories.collectAsState()/val categories by viewModel.allExpenseCategories.collectAsState()/g' app/src/main/java/com/example/ui/screens/ReportScreen.kt
