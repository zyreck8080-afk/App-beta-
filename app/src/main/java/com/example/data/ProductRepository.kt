package com.example.data

import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao, private val saleDao: SaleDao) {
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val allSales: Flow<List<Sale>> = saleDao.getAllSales()

    suspend fun getProductById(id: Int): Product? = productDao.getProductById(id)

    suspend fun insert(product: Product) = productDao.insertProduct(product)
    suspend fun delete(product: Product) = productDao.deleteProduct(product)

    suspend fun insert(sale: Sale) = saleDao.insertSale(sale)
    suspend fun delete(sale: Sale) = saleDao.deleteSale(sale)
}
