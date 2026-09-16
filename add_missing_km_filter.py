with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "r") as f:
    content = f.read()

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

content = content.replace("class LedgerViewModel(private val repository: LedgerRepository) : ViewModel() {", "class LedgerViewModel(private val repository: LedgerRepository) : ViewModel() {\n" + filter_code)

with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "w") as f:
    f.write(content)
