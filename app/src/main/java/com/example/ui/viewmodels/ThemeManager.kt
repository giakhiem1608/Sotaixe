package com.example.ui.viewmodels

import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AppThemePreset(
    val id: String,
    val name: String,
    val primaryHex: String,
    val heroBgHex: String
)

val APP_PRESETS = listOf(
    AppThemePreset("ocean_blue", "Ocean Blue", "#0284C7", "#0369A1"), // Light blue accent, deep blue hero
    AppThemePreset("emerald", "Emerald", "#10B981", "#047857"),
    AppThemePreset("midnight", "Midnight", "#3B82F6", "#0F172A"),
    AppThemePreset("violet", "Violet", "#8B5CF6", "#4C1D95"),
    AppThemePreset("graphite", "Graphite", "#52525B", "#18181B")
)

class ThemeManager(private val sharedPreferences: SharedPreferences?) {
    
    private val _currentPresetId = MutableStateFlow(sharedPreferences?.getString("preset_id", "ocean_blue") ?: "ocean_blue")
    
    // Custom colors if user overrides
    private val _customPrimary = MutableStateFlow(sharedPreferences?.getString("custom_primary", "") ?: "")
    private val _customHeroBg = MutableStateFlow(sharedPreferences?.getString("custom_hero", "") ?: "")
    
    private val _recentColors = MutableStateFlow(
        sharedPreferences?.getString("recent_colors", "")?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    )
    val recentColors: StateFlow<List<String>> = _recentColors
    
    // Derived states
    val activePreset: StateFlow<AppThemePreset> = MutableStateFlow(
        APP_PRESETS.find { it.id == _currentPresetId.value } ?: APP_PRESETS[0]
    )
    
    val primaryColorHex: StateFlow<String> = MutableStateFlow(
        if (_customPrimary.value.isNotEmpty()) _customPrimary.value else activePreset.value.primaryHex
    )
    
    val heroBgColorHex: StateFlow<String> = MutableStateFlow(
        if (_customHeroBg.value.isNotEmpty()) _customHeroBg.value else activePreset.value.heroBgHex
    )
    
    init {
        updateFlows()
    }
    
    fun setPreset(presetId: String) {
        sharedPreferences?.edit()?.apply {
            putString("preset_id", presetId)
            putString("custom_primary", "")
            putString("custom_hero", "")
            apply()
        }
        _currentPresetId.value = presetId
        _customPrimary.value = ""
        _customHeroBg.value = ""
        updateFlows()
    }
    
    fun setCustomColors(primaryHex: String, heroBgHex: String) {
        sharedPreferences?.edit()?.apply {
            putString("preset_id", "custom")
            putString("custom_primary", primaryHex)
            putString("custom_hero", heroBgHex)
            apply()
        }
        _currentPresetId.value = "custom"
        _customPrimary.value = primaryHex
        _customHeroBg.value = heroBgHex
        updateFlows()
        addRecentColor(heroBgHex)
    }
    
    fun addRecentColor(hex: String) {
        if (hex.isBlank()) return
        val current = _recentColors.value.toMutableList()
        current.remove(hex)
        current.add(0, hex)
        val newRecent = current.take(6)
        _recentColors.value = newRecent
        sharedPreferences?.edit()?.putString("recent_colors", newRecent.joinToString(","))?.apply()
    }
    
    private fun updateFlows() {
        val preset = APP_PRESETS.find { it.id == _currentPresetId.value } ?: AppThemePreset("custom", "Custom", _customPrimary.value, _customHeroBg.value)
        (activePreset as MutableStateFlow).value = preset
        (primaryColorHex as MutableStateFlow).value = if (_customPrimary.value.isNotEmpty()) _customPrimary.value else preset.primaryHex
        (heroBgColorHex as MutableStateFlow).value = if (_customHeroBg.value.isNotEmpty()) _customHeroBg.value else preset.heroBgHex
    }
}
