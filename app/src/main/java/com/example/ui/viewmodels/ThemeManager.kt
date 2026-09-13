package com.example.ui.viewmodels

import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeManager(private val sharedPreferences: SharedPreferences?) {
    private val _cardBgColor = MutableStateFlow(sharedPreferences?.getString("cardBgColor", "#111827") ?: "#111827")
    val cardBgColor: StateFlow<String> = _cardBgColor

    private val _incomeColor = MutableStateFlow(sharedPreferences?.getString("incomeColor", "") ?: "")
    val incomeColor: StateFlow<String> = _incomeColor
    
    private val _revenueColor = MutableStateFlow(sharedPreferences?.getString("revenueColor", "") ?: "")
    val revenueColor: StateFlow<String> = _revenueColor
    
    private val _expenseColor = MutableStateFlow(sharedPreferences?.getString("expenseColor", "") ?: "")
    val expenseColor: StateFlow<String> = _expenseColor

    private val _tipColor = MutableStateFlow(sharedPreferences?.getString("tipColor", "") ?: "")
    val tipColor: StateFlow<String> = _tipColor

    fun setCardColors(bgHex: String, incomeHex: String, revHex: String, expHex: String, tipHex: String = "") {
        sharedPreferences?.edit()?.apply {
            putString("cardBgColor", bgHex)
            putString("incomeColor", incomeHex)
            putString("revenueColor", revHex)
            putString("expenseColor", expHex)
            putString("tipColor", tipHex)
            apply()
        }
        _cardBgColor.value = bgHex
        _incomeColor.value = incomeHex
        _revenueColor.value = revHex
        _expenseColor.value = expHex
        _tipColor.value = tipHex
    }
}
