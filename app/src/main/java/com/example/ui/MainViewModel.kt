package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Product
import com.example.data.ProductRepository
import com.example.data.Sale
import com.example.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: ProductRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val primaryColor: StateFlow<String> = settingsRepository.primaryColorHex
    val typographyStyle: StateFlow<String> = settingsRepository.typographyStyle
    val isPrivacyMode: StateFlow<Boolean> = settingsRepository.isPrivacyMode
    val isOledMode: StateFlow<Boolean> = settingsRepository.isOledMode
    val isMinimalistMode: StateFlow<Boolean> = settingsRepository.isMinimalistMode
    val monthlyGoal: StateFlow<Float> = settingsRepository.monthlyGoal

    fun updatePrimaryColor(hex: String) = settingsRepository.setPrimaryColor(hex)
    fun updateTypographyStyle(style: String) = settingsRepository.setTypographyStyle(style)
    fun togglePrivacyMode() = settingsRepository.togglePrivacyMode()
    fun toggleOledMode() = settingsRepository.toggleOledMode()
    fun toggleMinimalistMode() = settingsRepository.toggleMinimalistMode()
    fun updateMonthlyGoal(goal: Float) = settingsRepository.setMonthlyGoal(goal)

    val allProducts: StateFlow<List<Product>> = repository.allProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allSales: StateFlow<List<Sale>> = repository.allSales.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addProduct(product: Product) {
        viewModelScope.launch { repository.insert(product) }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch { repository.delete(product) }
    }

    fun addSale(sale: Sale) {
        viewModelScope.launch { repository.insert(sale) }
    }

    fun deleteSale(sale: Sale) {
        viewModelScope.launch { repository.delete(sale) }
    }
}

class MainViewModelFactory(
    private val repository: ProductRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
