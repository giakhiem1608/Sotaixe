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

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val todaysExpenseEntries: StateFlow<List<ExpenseEntry>> = currentDateString.flatMapLatest { dateStr ->
        repository.getExpenseEntriesByDate(dateStr)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Aggregated stats for today
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val todaysTotalRevenue: StateFlow<Long> = currentDateString.flatMapLatest { dateStr ->
        repository.getTotalRevenueByDate(dateStr).map { it ?: 0L }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val todaysTotalExpense: StateFlow<Long> = currentDateString.flatMapLatest { dateStr ->
        repository.getTotalExpenseByDate(dateStr).map { it ?: 0L }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val todaysTotalTrips: StateFlow<Int> = currentDateString.flatMapLatest { dateStr ->
        repository.getTotalTripsByDate(dateStr).map { it ?: 0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val todaysTotalDuration: StateFlow<Float> = currentDateString.flatMapLatest { dateStr ->
        repository.getTotalDurationByDate(dateStr).map { it ?: 0f }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val todaysTotalDistance: StateFlow<Float> = currentDateString.flatMapLatest { dateStr ->
        repository.getTotalDistanceByDate(dateStr).map { it ?: 0f }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val todaysNetIncome: StateFlow<Long> = combine(todaysTotalRevenue, todaysTotalExpense) { rev, exp ->
        rev - exp
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

    fun addRevenue(sourceId: Int, amount: Long, trips: Int, duration: Float?, distance: Float?, note: String) {
        viewModelScope.launch {
            val dateStr = FormatUtils.formatDbDate(_currentDate.value)
            repository.insertRevenueEntry(
                RevenueEntry(
                    sourceId = sourceId,
                    amount = amount,
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
    
    fun deleteExpense(id: Int) {
        viewModelScope.launch { repository.deleteExpenseEntry(id) }
    }
    
    fun saveGoal(type: String, amount: Long) {
        viewModelScope.launch {
            repository.insertGoal(
                Goal(
                    type = type,
                    amount = amount,
                    monthString = _currentMonth.value
                )
            )
        }
    }
}

class LedgerViewModelFactory(private val repository: LedgerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LedgerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LedgerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
