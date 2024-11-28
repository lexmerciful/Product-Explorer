package com.lexmerciful.productexplorer.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.lexmerciful.productexplorer.common.Resource
import com.lexmerciful.productexplorer.data.repository.FakeProductRepository
import com.lexmerciful.productexplorer.domain.model.Product
import com.lexmerciful.productexplorer.domain.model.Rating
import com.lexmerciful.productexplorer.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeProductRepository: FakeProductRepository
    private lateinit var viewModel: ProductViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeProductRepository = FakeProductRepository()
        viewModel = ProductViewModel(fakeProductRepository)
        viewModel.refreshProduct()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchProducts emits loading and success`() = runTest {
        val products = listOf(
            Product(1, "Product 1", 10.0, "Description 1", "Category 1", "Image 1", Rating(4.5, 100)),
            Product(2, "Product 2", 20.0, "Description 2", "Category 2", "Image 2", Rating(3.8, 50))
        )

        viewModel.productListFlow.test {
            assertEquals(Resource.loading(emptyList<Product>()), awaitItem())
            assertEquals(Resource.success(products), awaitItem())
        }
    }

    @Test
    fun `fetchProducts emits loading and error on network failure`() = runTest {
        fakeProductRepository.setReturnNetworkError(true)
        viewModel.productListFlow.test {
            assertEquals(Resource.loading(emptyList<Product>()), awaitItem())
            assertEquals(Resource.error<List<Product>>("Network Error", emptyList()), awaitItem())
        }
    }

    @Test
    fun `fetchProducts emits success and then error with cache data on refresh with network failure`() = runTest {
        val successData = listOf(
            Product(1, "Product 1", 10.0, "Description 1", "Category 1", "Image 1", Rating(4.5, 100)),
            Product(2, "Product 2", 20.0, "Description 2", "Category 2", "Image 2", Rating(3.8, 50))
        )

        // Initial success with cache data saved
        viewModel.productListFlow.test {
            assertEquals(Resource.loading(emptyList<Product>()), awaitItem())
            assertEquals(Resource.success(successData), awaitItem())
        }

        // Simulate network error with cached data
        fakeProductRepository.setReturnNetworkError(true)
        viewModel.refreshProduct()
        viewModel.productListFlow.test {
            assertEquals(Resource.loading(successData), awaitItem())
            assertEquals(Resource.error("Network Error", successData), awaitItem())
        }
    }

    @Test
    fun `filteredProductsFlow filters products by category`() = runTest {
        viewModel.setFilter("Category 1")
        viewModel.filteredProductsFlow.test {
            assertEquals(emptyList<Product>(), awaitItem())
            val filteredProducts = listOf(
                Product(1, "Product 1", 10.0, "Description 1", "Category 1", "Image 1", Rating(4.5, 100))
            )
            assertEquals(filteredProducts, awaitItem())
        }
    }

    }