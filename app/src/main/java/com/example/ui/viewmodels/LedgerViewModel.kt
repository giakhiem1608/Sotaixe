package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.utils.FormatUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class LedgerViewModel(private val repository: LedgerRepository) : ViewModel() {

    private val _missingKmFilterActive = MutableStateFlow(false)
    val missingKmFilterActive: StateFlow<Boolean> = _missingKmFilterActive.asStateFlow()
    
    fun activateMissingKmFilter() {
        _missingKmFilterActive.value = true
    }
    
    fun clearMissingKmFilter() {
        _missingKmFilterActive.value = false
    }

    val themeManager = ThemeManager(repository.sharedPreferences)
    val primaryColorHex = themeManager.primaryColorHex
    val heroBgColorHex = themeManager.heroBgColorHex

    
    // Current selected date for Today screen (default to today)
    private val _currentDate = MutableStateFlow(System.currentTimeMillis())
    val currentDate: StateFlow<Long> = _currentDate.asStateFlow()
    
    // DB string format of current date for queries
    val currentDateString: Flow<String> = currentDate.map { FormatUtils.formatDbDate(it) }

    // Active Categories/Sources
    val activeRevenueSources = repository.activeRevenueSources.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val activeExpenseCategories = repository.activeExpenseCategories.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // Today's data
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val todaysRevenueEntries: StateFlow<List<RevenueEntry>> = currentDateString.flatMapLatest { dateStr ->
        repository.getRevenueEntriesByDate(dateStr)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allRevenueSources: StateFlow<List<RevenueSource>> = repository.getAllRevenueSourcesFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allExpenseCategories: StateFlow<List<ExpenseCategory>> = repository.getAllExpenseCategoriesFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val todaysExpenseEntries: StateFlow<List<ExpenseEntry>> = currentDateString.flatMapLatest { dateStr ->
        repository.getExpenseEntriesByDate(dateStr)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Aggregated stats for today
    val todaysTotalRevenue: StateFlow<Long> = todaysRevenueEntries.map { list -> list.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)
    
    val todaysTotalTip: StateFlow<Long> = todaysRevenueEntries.map { list -> list.sumOf { it.tipAmount ?: 0L } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val todaysTotalExpense: StateFlow<Long> = todaysExpenseEntries.map { list -> list.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val todaysTotalTrips: StateFlow<Int> = todaysRevenueEntries.map { list -> list.sumOf { it.trips } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    
    val todaysTotalDistance: StateFlow<Float> = todaysRevenueEntries.map { list -> list.sumOf { (it.distanceKm ?: 0f).toDouble() }.toFloat() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val todaysNetIncome: StateFlow<Long> = combine(todaysTotalRevenue, todaysTotalTip, todaysTotalExpense) { rev, tip, exp ->
        rev + tip - exp
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Current Month data for Report/Goals
    private val _currentMonth = MutableStateFlow(FormatUtils.formatDbMonth(System.currentTimeMillis()))
    val currentMonth: StateFlow<String> = _currentMonth.asStateFlow()
    
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val currentMonthGoal: StateFlow<Goal?> = currentMonth.flatMapLatest { monthStr ->
        repository.getGoalByMonth(monthStr)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val currentMonthRevenueEntries: StateFlow<List<RevenueEntry>> = currentMonth.flatMapLatest { monthStr ->
        repository.getRevenueEntriesByMonth(monthStr)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // History data for the selected month
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val historyRevenueEntries: StateFlow<List<RevenueEntry>> = currentMonth.flatMapLatest { monthStr ->
        repository.getRevenueEntriesByMonth(monthStr)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val historyExpenseEntries: StateFlow<List<ExpenseEntry>> = currentMonth.flatMapLatest { monthStr ->
        repository.getExpenseEntriesByMonth(monthStr)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun previousMonth() {
        val cal = Calendar.getInstance().apply { time = FormatUtils.parseDbMonth(_currentMonth.value) ?: Date() }
        cal.add(Calendar.MONTH, -1)
        _currentMonth.value = FormatUtils.formatDbMonth(cal.timeInMillis)
    }

    fun nextMonth() {
        val cal = Calendar.getInstance().apply { time = FormatUtils.parseDbMonth(_currentMonth.value) ?: Date() }
        cal.add(Calendar.MONTH, 1)
        _currentMonth.value = FormatUtils.formatDbMonth(cal.timeInMillis)
    }

    fun previousDay() {
        val cal = Calendar.getInstance().apply { timeInMillis = _currentDate.value }
        cal.add(Calendar.DAY_OF_YEAR, -1)
        _currentDate.value = cal.timeInMillis
    }

    fun nextDay() {
        val cal = Calendar.getInstance().apply { timeInMillis = _currentDate.value }
        cal.add(Calendar.DAY_OF_YEAR, 1)
        _currentDate.value = cal.timeInMillis
    }
    
    fun setDate(timestamp: Long) {
        _currentDate.value = timestamp
    }
    
    fun setMonth(monthString: String) {
        _currentMonth.value = monthString
    }

    fun addRevenue(sourceId: Int, amount: Long, tipAmount: Long?, trips: Int, duration: Float?, distance: Float?, note: String) {
        viewModelScope.launch {
            val dateStr = FormatUtils.formatDbDate(_currentDate.value)
            repository.insertRevenueEntry(
                RevenueEntry(
                    sourceId = sourceId,
                    amount = amount,
                    tipAmount = tipAmount ?: 0L,
                    trips = trips,
                    durationHrs = duration,
                    distanceKm = distance,
                    note = note,
                    timestamp = _currentDate.value,
                    dateString = dateStr
                )
            )
        }
    }
    
    fun updateRevenueEntry(entry: RevenueEntry) {
        viewModelScope.launch { repository.updateRevenueEntry(entry) }
    }

    fun deleteRevenue(id: Int) {
        viewModelScope.launch { repository.deleteRevenueEntry(id) }
    }

    fun addExpense(categoryId: Int, amount: Long, note: String) {
        viewModelScope.launch {
            val dateStr = FormatUtils.formatDbDate(_currentDate.value)
            repository.insertExpenseEntry(
                ExpenseEntry(
                    categoryId = categoryId,
                    amount = amount,
                    note = note,
                    timestamp = _currentDate.value,
                    dateString = dateStr
                )
            )
        }
    }
    
    fun updateExpenseEntry(entry: ExpenseEntry) {
        viewModelScope.launch { repository.updateExpenseEntry(entry) }
    }

    fun deleteExpense(id: Int) {
        viewModelScope.launch { repository.deleteExpenseEntry(id) }
    }
    
    fun deleteGoal(goal: Goal) {
        viewModelScope.launch { repository.deleteGoal(goal) }
    }
    fun saveGoal(type: String, amount: Long) {
        viewModelScope.launch {
            val currentId = currentMonthGoal.value?.id ?: 0
            repository.insertGoal(
                Goal(
                    id = currentId,
                    type = type,
                    amount = amount,
                    monthString = _currentMonth.value
                )
            )
        }
    }

    fun addRevenueSource(name: String, colorHex: String) {
        viewModelScope.launch {
            repository.insertRevenueSource(RevenueSource(name = name, colorHex = colorHex, isDefault = false, isActive = true))
        }
    }

    fun deleteRevenueSource(source: RevenueSource) {
        viewModelScope.launch {
            if (repository.countRevenueEntries(source.id) > 0) {
                repository.updateRevenueSource(source.copy(isActive = false))
            } else {
                repository.deleteRevenueSource(source)
            }
        }
    }

    fun hideRevenueSource(source: RevenueSource) {
        viewModelScope.launch {
            repository.updateRevenueSource(source.copy(isActive = false))
        }
    }

    fun addExpenseCategory(name: String, iconName: String) {
        viewModelScope.launch {
            repository.insertExpenseCategory(ExpenseCategory(name = name, iconName = iconName, isDefault = false, isActive = true))
        }
    }

    fun deleteExpenseCategory(category: ExpenseCategory) {
        viewModelScope.launch {
            if (repository.countExpenseEntries(category.id) > 0) {
                repository.updateExpenseCategory(category.copy(isActive = false))
            } else {
                repository.deleteExpenseCategory(category)
            }
        }
    }

    fun hideExpenseCategory(category: ExpenseCategory) {
        viewModelScope.launch {
            repository.updateExpenseCategory(category.copy(isActive = false))
        }
    }

    fun updateRevenueSource(source: RevenueSource) {
        viewModelScope.launch {
            repository.updateRevenueSource(source)
        }
    }

    fun updateExpenseCategory(category: ExpenseCategory) {
        viewModelScope.launch {
            repository.updateExpenseCategory(category)
        }
    }

    // Backup & Restore
    fun generateBackupData(onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val data = BackupData(
                    sources = repository.getAllRevenueSources(),
                    categories = repository.getAllExpenseCategories(),
                    revenueEntries = repository.getAllRevenueEntries(),
                    expenseEntries = repository.getAllExpenseEntries(),
                    goals = repository.getAllGoals()
                )
                val moshi = com.squareup.moshi.Moshi.Builder().build()
                val adapter = moshi.adapter(BackupData::class.java)
                onResult(adapter.toJson(data))
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(null)
            }
        }
    }

    suspend fun resetAllData() {
        repository.clearGoals()
        repository.clearRevenueEntries()
        repository.clearExpenseEntries()
        repository.clearRevenueSources()
        repository.clearExpenseCategories()
        // Phục hồi lại mặc định
        repository.insertRevenueSource(com.example.data.RevenueSource(name = "Xanh SM", colorHex = "#8B5CF6", isDefault = true))
        repository.insertRevenueSource(com.example.data.RevenueSource(name = "Grab", colorHex = "#16A34A", isDefault = true))
        repository.insertRevenueSource(com.example.data.RevenueSource(name = "Khách ngoài", colorHex = "#3B82F6", isDefault = true))
        repository.insertExpenseCategory(com.example.data.ExpenseCategory(name = "Sạc xe", iconName = "ev_station", isDefault = true))
        repository.insertExpenseCategory(com.example.data.ExpenseCategory(name = "Ăn uống", iconName = "restaurant", isDefault = true))
        repository.insertExpenseCategory(com.example.data.ExpenseCategory(name = "Gửi xe", iconName = "local_parking", isDefault = true))
        repository.insertExpenseCategory(com.example.data.ExpenseCategory(name = "Cầu đường", iconName = "add_road", isDefault = true))
        repository.insertExpenseCategory(com.example.data.ExpenseCategory(name = "Rửa xe", iconName = "local_car_wash", isDefault = true))
        repository.insertExpenseCategory(com.example.data.ExpenseCategory(name = "Bảo dưỡng", iconName = "build", isDefault = true))
        repository.insertExpenseCategory(com.example.data.ExpenseCategory(name = "Điện thoại / 4G", iconName = "phone_android", isDefault = true))
        repository.insertExpenseCategory(com.example.data.ExpenseCategory(name = "Khác", iconName = "more_horiz", isDefault = true))
    }

    fun restoreBackupData(json: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val moshi = com.squareup.moshi.Moshi.Builder().build()
                val adapter = moshi.adapter(BackupData::class.java)
                val data = adapter.fromJson(json)
                if (data != null) {
                    repository.clearRevenueSources()
                    repository.clearExpenseCategories()
                    repository.clearRevenueEntries()
                    repository.clearExpenseEntries()
                    repository.clearGoals()

                    data.sources.forEach { repository.insertRevenueSource(it) }
                    data.categories.forEach { repository.insertExpenseCategory(it) }
                    data.revenueEntries.forEach { repository.insertRevenueEntry(it) }
                    data.expenseEntries.forEach { repository.insertExpenseEntry(it) }
                    data.goals.forEach { val currentId = currentMonthGoal.value?.id ?: 0
            repository.insertGoal(it) }
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    // Export CSV
    fun exportXlsxDataRange(startDateStr: String, endDateStr: String, label: String, outputStream: java.io.OutputStream, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val sources = repository.getAllRevenueSources()
                val categories = repository.getAllExpenseCategories()
                val allRev = repository.getAllRevenueEntries(); val revEntries = allRev.filter { it.dateString in startDateStr..endDateStr }
                val allExp = repository.getAllExpenseEntries(); val expEntries = allExp.filter { it.dateString in startDateStr..endDateStr }

                val sourceMap = sources.associateBy { it.id }
                val categoryMap = categories.associateBy { it.id }

                val wb = org.dhatim.fastexcel.Workbook(outputStream, "SoTaiXe", "1.0")
                
                val wsOverview = wb.newWorksheet("Tong quan")
                wsOverview.value(0, 0, "BAO CAO: $label")
                wsOverview.style(0, 0).bold().set()
                
                val totalRev = revEntries.sumOf { it.amount }
                val totalExp = expEntries.sumOf { it.amount }
                val totalTip = revEntries.sumOf { it.tipAmount ?: 0L }
                val totalTrips = revEntries.sumOf { it.trips }
                
                wsOverview.value(2, 0, "Tong doanh thu")
                wsOverview.value(2, 1, totalRev)
                wsOverview.value(3, 0, "Tong Tip")
                wsOverview.value(3, 1, totalTip)
                wsOverview.value(4, 0, "Tong tien nhan")
                wsOverview.value(4, 1, totalRev + totalTip)
                wsOverview.value(5, 0, "Tong chi phi")
                wsOverview.value(5, 1, totalExp)
                wsOverview.value(6, 0, "Thu nhap rong")
                wsOverview.value(6, 1, totalRev + totalTip - totalExp)
                wsOverview.value(7, 0, "Tong so cuoc")
                wsOverview.value(7, 1, totalTrips)

                val wsRev = wb.newWorksheet("Doanh thu")
                val revHeaders = listOf("Ngay", "Nguon", "So tien", "Tien Tip", "So cuoc", "Km", "Gio chay", "Ghi chu")
                revHeaders.forEachIndexed { i, header -> 
                    wsRev.value(0, i, header)
                    wsRev.style(0, i).bold().set()
                }
                
                revEntries.forEachIndexed { rowIdx, rev ->
                    val r = rowIdx + 1
                    val sourceName = sourceMap[rev.sourceId]?.name ?: "Khac"
                    val date = FormatUtils.formatDate(FormatUtils.parseDbDate(rev.dateString))
                    wsRev.value(r, 0, date)
                    wsRev.value(r, 1, sourceName)
                    wsRev.value(r, 2, rev.amount)
                    wsRev.value(r, 3, rev.tipAmount ?: 0L)
                    wsRev.value(r, 4, rev.trips)
                    if (rev.distanceKm != null) wsRev.value(r, 5, rev.distanceKm)
                    if (rev.durationHrs != null) wsRev.value(r, 6, rev.durationHrs)
                    wsRev.value(r, 7, rev.note)
                }

                val wsExp = wb.newWorksheet("Chi phi")
                val expHeaders = listOf("Ngay", "Danh muc", "So tien", "Ghi chu")
                expHeaders.forEachIndexed { i, header -> 
                    wsExp.value(0, i, header)
                    wsExp.style(0, i).bold().set()
                }

                expEntries.forEachIndexed { rowIdx, exp ->
                    val r = rowIdx + 1
                    val catName = categoryMap[exp.categoryId]?.name ?: "Khac"
                    val date = FormatUtils.formatDate(FormatUtils.parseDbDate(exp.dateString))
                    wsExp.value(r, 0, date)
                    wsExp.value(r, 1, catName)
                    wsExp.value(r, 2, exp.amount)
                    wsExp.value(r, 3, exp.note)
                }

                wb.finish()
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

}

class LedgerViewModelFactory(private val repository: LedgerRepository) : ViewModelProvider.Factory {
    val themeManager = ThemeManager(repository.sharedPreferences)
    val primaryColorHex = themeManager.primaryColorHex
    val heroBgColorHex = themeManager.heroBgColorHex

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LedgerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LedgerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
