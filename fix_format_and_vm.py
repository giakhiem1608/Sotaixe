import re

# Fix LedgerViewModel.kt duplicate functions
with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "r") as f:
    vm_content = f.read()

# Replace all occurrences of the filter code with nothing
filter_code = """
    private val _missingKmFilterActive = MutableStateFlow(false)
    val missingKmFilterActive: StateFlow<Boolean> = _missingKmFilterActive.asStateFlow()
    
    fun activateMissingKmFilter() {
        _missingKmFilterActive.value = true
    }
    
    fun clearMissingKmFilter() {
        _missingKmFilterActive.value = false
    }
"""

vm_content = vm_content.replace(filter_code, "")

# Insert it back exactly once
vm_content = vm_content.replace("class LedgerViewModel(private val repository: LedgerRepository) : ViewModel() {", "class LedgerViewModel(private val repository: LedgerRepository) : ViewModel() {\n" + filter_code, 1)

with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "w") as f:
    f.write(vm_content)

# Fix FormatUtils.kt
with open("app/src/main/java/com/example/utils/FormatUtils.kt", "r") as f:
    utils_content = f.read()

new_methods = """
    fun formatDisplayMonth(monthString: String): String {
        val date = parseDbMonth(monthString) ?: return ""
        return monthFormat.format(date)
    }
    
    fun formatDisplayDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }
"""

utils_content = utils_content.replace("}", new_methods + "\n}")

with open("app/src/main/java/com/example/utils/FormatUtils.kt", "w") as f:
    f.write(utils_content)

# Revert previous sed changes in UI files
import os
os.system("sed -i 's/formatMonth(currentMonth/formatDisplayMonth(currentMonth/g' app/src/main/java/com/example/ui/screens/HistoryScreen.kt app/src/main/java/com/example/ui/screens/ReportScreen.kt")
os.system("sed -i 's/formatDate(/formatDisplayDate(/g' app/src/main/java/com/example/ui/screens/HistoryScreen.kt app/src/main/java/com/example/ui/screens/TodayScreen.kt app/src/main/java/com/example/ui/screens/OtherScreen.kt")

