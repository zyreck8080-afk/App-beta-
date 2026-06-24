package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("lumina_settings", Context.MODE_PRIVATE)

    private val _primaryColorHex = MutableStateFlow(prefs.getString("primary_color", "#FFFFB6C1") ?: "#FFFFB6C1")
    val primaryColorHex: StateFlow<String> = _primaryColorHex.asStateFlow()

    private val _typographyStyle = MutableStateFlow(prefs.getString("typography_style", "SansSerif") ?: "SansSerif")
    val typographyStyle: StateFlow<String> = _typographyStyle.asStateFlow()

    private val _isPrivacyMode = MutableStateFlow(prefs.getBoolean("privacy_mode", false))
    val isPrivacyMode: StateFlow<Boolean> = _isPrivacyMode.asStateFlow()

    private val _isOledMode = MutableStateFlow(prefs.getBoolean("oled_mode", false))
    val isOledMode: StateFlow<Boolean> = _isOledMode.asStateFlow()

    fun setPrimaryColor(hex: String) {
        prefs.edit().putString("primary_color", hex).apply()
        _primaryColorHex.value = hex
    }

    fun setTypographyStyle(style: String) {
        prefs.edit().putString("typography_style", style).apply()
        _typographyStyle.value = style
    }

    fun togglePrivacyMode() {
        val newVal = !_isPrivacyMode.value
        prefs.edit().putBoolean("privacy_mode", newVal).apply()
        _isPrivacyMode.value = newVal
    }

    fun toggleOledMode() {
        val newVal = !_isOledMode.value
        prefs.edit().putBoolean("oled_mode", newVal).apply()
        _isOledMode.value = newVal
    }
}
