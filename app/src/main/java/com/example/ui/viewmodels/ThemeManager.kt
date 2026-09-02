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
