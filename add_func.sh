sed -i '/val expenseColor = themeManager.expenseColor/a \
\
    fun updateCardColors(bgHex: String, incomeHex: String, revHex: String, expHex: String) {\
        themeManager.setCardColors(bgHex, incomeHex, revHex, expHex)\
    }' app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt
