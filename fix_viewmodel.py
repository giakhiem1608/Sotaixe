import re

with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "r") as f:
    content = f.read()

insert = """    
    private val _activeMissingKmFilter = MutableStateFlow(false)
    val activeMissingKmFilter: StateFlow<Boolean> = _activeMissingKmFilter.asStateFlow()
    
    fun activateMissingKmFilter() {
        _activeMissingKmFilter.value = true
    }
    
    fun clearMissingKmFilter() {
        _activeMissingKmFilter.value = false
    }
"""

content = content.replace("class LedgerViewModel(private val repository: LedgerRepository) : ViewModel() {", "class LedgerViewModel(private val repository: LedgerRepository) : ViewModel() {\n" + insert)

with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "w") as f:
    f.write(content)

