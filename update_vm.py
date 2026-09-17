import re

with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "r") as f:
    content = f.read()

# Add all data flows to ViewModel
new_flows = """
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val allRevenueEntries: StateFlow<List<RevenueEntry>> = repository.getAllRevenueEntriesFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val allExpenseEntries: StateFlow<List<ExpenseEntry>> = repository.getAllExpenseEntriesFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
"""

content = content.replace(
    '// Current Month data for Report/Goals',
    new_flows + '\n    // Current Month data for Report/Goals'
)

with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "w") as f:
    f.write(content)

print("Updated ViewModel")
