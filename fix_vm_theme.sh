cat << 'INNER_EOF' > app/src/main/java/com/example/ui/viewmodels/ThemeManager.kt
package com.example.ui.viewmodels

import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeManager(private val sharedPreferences: SharedPreferences?) {
    private val _revenueThemeColor = MutableStateFlow(sharedPreferences?.getString("revenueThemeColor", "#16A34A") ?: "#16A34A")
    val revenueThemeColor: StateFlow<String> = _revenueThemeColor

    fun setRevenueThemeColor(colorHex: String) {
        sharedPreferences?.edit()?.putString("revenueThemeColor", colorHex)?.apply()
        _revenueThemeColor.value = colorHex
    }
}
INNER_EOF

sed -i '/class LedgerViewModel/a \    val themeManager = ThemeManager(repository.sharedPreferences)\n    val revenueThemeColor = themeManager.revenueThemeColor' app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt
