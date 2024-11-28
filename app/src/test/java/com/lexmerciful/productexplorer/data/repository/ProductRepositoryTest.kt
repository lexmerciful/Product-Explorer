package com.lexmerciful.productexplorer.data.repository

import com.lexmerciful.productexplorer.common.Resource
import com.lexmerciful.productexplorer.domain.model.Product
import com.lexmerciful.productexplorer.domain.model.Rating
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductRepositoryTest {

    private lateinit var repository: FakeProductRepository

    @Before
    fun setUp() {
        repository = FakeProductRepository()
    }

    @Test
    fun `repository emits loading and success when fetching products successfully`() = runTest(UnconfinedTestDispatcher()) {
        repository.setReturnNetworkError(false)
        val products = listOf(
            Product(1, "Product 1", 10.0, "Description 1", "Category 1", "Image 1", Rating(4.5, 100)),
            Product(2, "Product 2", 20.0, "Description 2", "Category 2", "Image 2", Rating(3.8, 50))
        )

        val emissions = repository.getProducts().toList()

        assertEquals(emissions[0], Resource.loading(emptyList<Product>()))
        assertEquals(emissions[1], Resource.success(products))
    }

    @Test
    fun `repository emits loading and error on network failure`() = runTest(UnconfinedTestDispatcher()) {
        repository.setReturnNetworkError(true)

        val emissions = repository.getProducts().toList()

        assertEquals(emissions[0], Resource.loading(emptyList<Product>()))
        assertEquals(emissions[1], Resource.error<List<Product>>("Network Error", emptyList()))
    }

}