package com.lexmerciful.productexplorer.data.repository

import app.cash.turbine.test
import com.lexmerciful.productexplorer.common.Resource
import com.lexmerciful.productexplorer.data.local.ProductDao
import com.lexmerciful.productexplorer.data.remote.ProductApiService
import com.lexmerciful.productexplorer.domain.model.Product
import com.lexmerciful.productexplorer.domain.model.Rating
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response
import java.io.IOException

class ProductRepositoryTest {

    @Mock
    private lateinit var productApiService: ProductApiService

    @Mock
    private lateinit var productDao: ProductDao
    private lateinit var repository: ProductRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = ProductRepositoryImpl(productApiService, productDao)
    }

    @Test
    fun `getProducts emits loading and success`() = runTest {
        val products = listOf(
            Product(1, "Product 1", 10.0, "Description 1", "Category 1", "Image 1", Rating(4.5, 100)),
            Product(2, "Product 2", 20.0, "Description 2", "Category 2", "Image 2", Rating(3.8, 50))
        )
        `when`(productDao.getAllProducts()).thenReturn(flowOf(products))
        `when`(productApiService.getProducts()).thenReturn(Response.success(products))

        repository.getProducts().test {
            assertEquals(Resource.loading(products), awaitItem())
            assertEquals(Resource.success(products), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getProducts emits loading and error`() = runTest {
        val products = listOf(
            Product(1, "Product 1", 10.0, "Description 1", "Category 1", "Image 1", Rating(4.5, 100)),
            Product(2, "Product 2", 20.0, "Description 2", "Category 2", "Image 2", Rating(3.8, 50))
        )

        `when`(productDao.getAllProducts()).thenReturn(flowOf(products))
        `when`(productApiService.getProducts()).thenThrow(RuntimeException())

        repository.getProducts().test {
            assertEquals(Resource.loading(products), awaitItem())
            assertEquals(Resource.error("Unable to reach server. Please check your connection", products), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

}