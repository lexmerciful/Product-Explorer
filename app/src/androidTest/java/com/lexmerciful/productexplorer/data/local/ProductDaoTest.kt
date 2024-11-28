package com.lexmerciful.productexplorer.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.lexmerciful.productexplorer.domain.model.Product
import com.lexmerciful.productexplorer.domain.model.Rating
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class ProductDaoTest {

    private lateinit var database: ProductDatabase
    private lateinit var dao: ProductDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ProductDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.productDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetAllProducts() = runTest {
        val products = listOf(
            Product(1, "Product 1", 100.0, "Desc 1", "Category 1", "Image 1", Rating(4.5, 10)),
            Product(2, "Product 2", 200.0, "Desc 2", "Category 2", "Image 2", Rating(3.8, 5))
        )

        dao.insertAllProduct(products)

        val allProducts = dao.getAllProducts().first()
        assertThat(allProducts).isEqualTo(products)
    }

    @Test
    fun getProductById() = runTest {
        val product = Product(1, "Product 1", 100.0, "Desc 1", "Category 1", "Image 1", Rating(4.5, 10))
        dao.insertAllProduct(listOf(product))
        val getProduct = dao.getProductById(1).first()

        assertThat(getProduct).isEqualTo(product)
    }

    @Test
    fun insertAndDeleteAllProducts() = runTest {
        val products = listOf(
                Product(1, "Product 1", 100.0, "Desc 1", "Category 1", "Image 1", Rating(4.5, 10)),
        Product(2, "Product 2", 200.0, "Desc 2", "Category 2", "Image 2", Rating(3.8, 5))
        )

        dao.insertAllProduct(products)
        dao.deleteAllProduct()

        val allProducts = dao.getAllProducts().first()
        assertThat(allProducts).isEmpty()
    }
}