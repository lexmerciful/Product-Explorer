package com.lexmerciful.productexplorer.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.lexmerciful.productexplorer.common.Resource
import com.lexmerciful.productexplorer.domain.model.Product
import com.lexmerciful.productexplorer.domain.model.Rating
import com.lexmerciful.productexplorer.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var productRepository: ProductRepository

    private val productViewModel: ProductViewModel by lazy {
        ProductViewModel(productRepository)
    }
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
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
        `when`(productRepository.getProducts()).thenReturn(flowOf(Resource.loading(), Resource.success(products)))

        //doReturn(flowOf(Resource.success(products))).`when`(productRepository.getProducts())
        productViewModel.productListFlow.test {
            assertEquals(Resource.loading(null), awaitItem())
            assertEquals(Resource.success(products), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetchProducts emits loading and error`() = runTest {
        val products = listOf(
            Product(1, "Product 1", 10.0, "Description 1", "Category 1", "Image 1", Rating(4.5, 100)),
            Product(2, "Product 2", 20.0, "Description 2", "Category 2", "Image 2", Rating(3.8, 50))
        )
        `when`(productRepository.getProducts()).thenReturn(flowOf(Resource.error("Network Error", products)))

        productViewModel.productListFlow.test {
            assertEquals(Resource.loading(null), awaitItem())
            assertEquals(Resource.error("Network Error", products), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setFilter filters products based on category correctly`() = runTest {
        val products = listOf(
            Product(1, "Product 1", 10.0, "Description 1", "Category 1", "Image 1", Rating(4.5, 100)),
            Product(2, "Product 2", 20.0, "Description 2", "Category 2", "Image 2", Rating(3.8, 50))
        )
        val filteredProducts = listOf(Product(1, "Product 1", 10.0, "Description 1", "Category 1", "Image 1", Rating(4.5, 100)))
        `when`(productRepository.getProducts()).thenReturn(flowOf(Resource.success(products)))

        productViewModel.setFilter("Category 1")

        productViewModel.filteredProductsFlow.test {
            assertEquals(emptyList<Product>(), awaitItem())
            assertEquals(filteredProducts, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

}