package com.lexmerciful.productexplorer.data.repository

import android.util.Log
import com.lexmerciful.productexplorer.common.Resource
import com.lexmerciful.productexplorer.data.local.ProductDao
import com.lexmerciful.productexplorer.data.performGetOperation
import com.lexmerciful.productexplorer.data.remote.ProductApiService
import com.lexmerciful.productexplorer.domain.model.Product
import com.lexmerciful.productexplorer.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productApiService: ProductApiService,
    private val productDao: ProductDao
): ProductRepository {
    override suspend fun getProducts(): Flow<Resource<List<Product>>> = performGetOperation(
        databaseQuery = { productDao.getAllProducts() },
        networkCall = { getProductsFromNetwork() },
        saveCallResult = { productDao.insertAllProduct(it) }
    )

    private suspend fun getProductsFromNetwork(): Resource<List<Product>> {
        return try {
            val response = productApiService.getProducts()
            if (response.isSuccessful) {
                Log.d(TAG, "Success ${response.body()}")
                Resource.success(response.body() ?: emptyList())
            } else {
                Resource.error("Unable to reach server. Please check your connection")
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error ${e.localizedMessage}")
            Resource.error("Unable to reach server. Please check your connection")
        }
    }

    companion object {
        private val TAG = ProductRepository::class.java.simpleName
    }

}