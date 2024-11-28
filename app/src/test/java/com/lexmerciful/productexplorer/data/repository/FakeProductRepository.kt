package com.lexmerciful.productexplorer.data.repository

import com.lexmerciful.productexplorer.common.Resource
import com.lexmerciful.productexplorer.domain.model.Product
import com.lexmerciful.productexplorer.domain.model.Rating
import com.lexmerciful.productexplorer.domain.repository.ProductRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class FakeProductRepository: ProductRepository {

    private val productList = mutableListOf<Product>()
    private val productListFlow = MutableStateFlow<List<Product>>(productList)

    private var shouldReturnNetworkError = false

    fun setReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    private fun updateFlowData() {
        productListFlow.value = productList
    }

    override suspend fun getProducts(): Flow<Resource<List<Product>>> {
        return flow {
            emit(Resource.loading(productListFlow.value.toMutableList()))
            delay(1000) // To simulate network call
            if (shouldReturnNetworkError) {
                emit(Resource.error("Network Error", productListFlow.value.toMutableList()))
            } else {
                val apiProducts = listOf(
                    Product(1, "Product 1", 10.0, "Description 1", "Category 1", "Image 1", Rating(4.5, 100)),
                    Product(2, "Product 2", 20.0, "Description 2", "Category 2", "Image 2", Rating(3.8, 50))
                )

                productList.clear()
                productList.addAll(apiProducts)
                updateFlowData()
                emit(Resource.success(productList))
            }
        }
    }

}