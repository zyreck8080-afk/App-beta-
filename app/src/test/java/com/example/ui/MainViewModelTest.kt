package com.example.ui

import com.example.data.Product
import com.example.data.ProductRepository
import com.example.data.Sale
import com.example.data.SettingsRepository
import com.example.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var productRepository: ProductRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: MainViewModel

    private val primaryColorFlow = MutableStateFlow("#FFFFB6C1")
    private val typographyStyleFlow = MutableStateFlow("SansSerif")
    private val isPrivacyModeFlow = MutableStateFlow(false)
    private val isOledModeFlow = MutableStateFlow(false)

    private val allProductsFlow = MutableStateFlow<List<Product>>(emptyList())
    private val allSalesFlow = MutableStateFlow<List<Sale>>(emptyList())

    @Before
    fun setup() {
        productRepository = mockk(relaxed = true) {
            every { allProducts } returns allProductsFlow
            every { allSales } returns allSalesFlow
        }

        settingsRepository = mockk(relaxed = true) {
            every { primaryColorHex } returns primaryColorFlow
            every { typographyStyle } returns typographyStyleFlow
            every { isPrivacyMode } returns isPrivacyModeFlow
            every { isOledMode } returns isOledModeFlow
        }

        viewModel = MainViewModel(productRepository, settingsRepository)
    }

    @Test
    fun `initial state reflects repository values`() = runTest {
        assertEquals("#FFFFB6C1", viewModel.primaryColor.value)
        assertEquals("SansSerif", viewModel.typographyStyle.value)
        assertFalse(viewModel.isPrivacyMode.value)
        assertFalse(viewModel.isOledMode.value)

        // Starts empty based on SharingStarted and initialValue
        assertEquals(emptyList<Product>(), viewModel.allProducts.value)
        assertEquals(emptyList<Sale>(), viewModel.allSales.value)
    }

    @Test
    fun `updatePrimaryColor delegates to settingsRepository`() {
        val newColor = "#FF0000"
        viewModel.updatePrimaryColor(newColor)
        verify { settingsRepository.setPrimaryColor(newColor) }
    }

    @Test
    fun `updateTypographyStyle delegates to settingsRepository`() {
        val newStyle = "Monospace"
        viewModel.updateTypographyStyle(newStyle)
        verify { settingsRepository.setTypographyStyle(newStyle) }
    }

    @Test
    fun `togglePrivacyMode delegates to settingsRepository`() {
        viewModel.togglePrivacyMode()
        verify { settingsRepository.togglePrivacyMode() }
    }

    @Test
    fun `toggleOledMode delegates to settingsRepository`() {
        viewModel.toggleOledMode()
        verify { settingsRepository.toggleOledMode() }
    }

    @Test
    fun `addProduct delegates to productRepository`() = runTest {
        val product = Product(id = 1, name = "Test", costBase = 10.0, costExtra = 2.0, unit = "kg")
        viewModel.addProduct(product)

        // Allow coroutine to execute
        advanceUntilIdle()

        coVerify { productRepository.insert(product) }
    }

    @Test
    fun `deleteProduct delegates to productRepository`() = runTest {
        val product = Product(id = 1, name = "Test", costBase = 10.0, costExtra = 2.0, unit = "kg")
        viewModel.deleteProduct(product)

        // Allow coroutine to execute
        advanceUntilIdle()

        coVerify { productRepository.delete(product) }
    }

    @Test
    fun `addSale delegates to productRepository`() = runTest {
        val sale = Sale(id = 1, productId = 1, productName = "Test", quantity = 2, salePriceTotal = 30.0, costBaseTotal = 20.0)
        viewModel.addSale(sale)

        // Allow coroutine to execute
        advanceUntilIdle()

        coVerify { productRepository.insert(sale) }
    }

    @Test
    fun `deleteSale delegates to productRepository`() = runTest {
        val sale = Sale(id = 1, productId = 1, productName = "Test", quantity = 2, salePriceTotal = 30.0, costBaseTotal = 20.0)
        viewModel.deleteSale(sale)

        // Allow coroutine to execute
        advanceUntilIdle()

        coVerify { productRepository.delete(sale) }
    }

    @Test
    fun `allProducts updates when repository flow updates`() = runTest {
        val product = Product(id = 1, name = "Test", costBase = 10.0, costExtra = 2.0, unit = "kg")

        // Emit new list
        allProductsFlow.value = listOf(product)

        // Collect to start the flow since it uses SharingStarted.WhileSubscribed
        val job = launch {
            viewModel.allProducts.collect {}
        }

        advanceUntilIdle()

        // Verify ViewModel received it
        assertEquals(listOf(product), viewModel.allProducts.value)

        job.cancel()
    }

    @Test
    fun `allSales updates when repository flow updates`() = runTest {
        val sale = Sale(id = 1, productId = 1, productName = "Test", quantity = 2, salePriceTotal = 30.0, costBaseTotal = 20.0)

        // Emit new list
        allSalesFlow.value = listOf(sale)

        // Collect to start the flow since it uses SharingStarted.WhileSubscribed
        val job = launch {
            viewModel.allSales.collect {}
        }

        advanceUntilIdle()

        // Verify ViewModel received it
        assertEquals(listOf(sale), viewModel.allSales.value)

        job.cancel()
    }
}
