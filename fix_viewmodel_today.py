import re

with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "r") as f:
    content = f.read()

target = r"""    @OptIn\(kotlinx\.coroutines\.ExperimentalCoroutinesApi::class\)\n    val todaysTotalRevenue: StateFlow<Long> = currentDateString\.flatMapLatest \{ dateStr ->\n        repository\.getTotalRevenueByDate\(dateStr\)\.map \{ it \?: 0L \}\n    \}\.stateIn\(viewModelScope, SharingStarted\.WhileSubscribed\(5000\), 0L\)\n\n    @OptIn\(kotlinx\.coroutines\.ExperimentalCoroutinesApi::class\)\n    val todaysTotalExpense: StateFlow<Long> = currentDateString\.flatMapLatest \{ dateStr ->\n        repository\.getTotalExpenseByDate\(dateStr\)\.map \{ it \?: 0L \}\n    \}\.stateIn\(viewModelScope, SharingStarted\.WhileSubscribed\(5000\), 0L\)\n\n    @OptIn\(kotlinx\.coroutines\.ExperimentalCoroutinesApi::class\)\n    val todaysTotalTrips: StateFlow<Int> = currentDateString\.flatMapLatest \{ dateStr ->\n        repository\.getTotalTripsByDate\(dateStr\)\.map \{ it \?: 0 \}\n    \}\.stateIn\(viewModelScope, SharingStarted\.WhileSubscribed\(5000\), 0\)\n    \n    @OptIn\(kotlinx\.coroutines\.ExperimentalCoroutinesApi::class\)\n    val todaysTotalDuration: StateFlow<Float> = currentDateString\.flatMapLatest \{ dateStr ->\n        repository\.getTotalDurationByDate\(dateStr\)\.map \{ it \?: 0f \}\n    \}\.stateIn\(viewModelScope, SharingStarted\.WhileSubscribed\(5000\), 0f\)\n\n    @OptIn\(kotlinx\.coroutines\.ExperimentalCoroutinesApi::class\)\n    val todaysTotalDistance: StateFlow<Float> = currentDateString\.flatMapLatest \{ dateStr ->\n        repository\.getTotalDistanceByDate\(dateStr\)\.map \{ it \?: 0f \}\n    \}\.stateIn\(viewModelScope, SharingStarted\.WhileSubscribed\(5000\), 0f\)\n\n    val todaysNetIncome: StateFlow<Long> = combine\(todaysTotalRevenue, todaysTotalExpense\) \{ rev, exp ->\n        rev - exp\n    \}\.stateIn\(viewModelScope, SharingStarted\.WhileSubscribed\(5000\), 0L\)"""

replace = """    val todaysTotalRevenue: StateFlow<Long> = todaysRevenueEntries.map { list -> list.sumOf { it.amount } }
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
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)"""

content = re.sub(target, replace, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "w") as f:
    f.write(content)

