package com.lexmerciful.productexplorer.data.remote

import com.lexmerciful.productexplorer.common.Resource
import com.lexmerciful.productexplorer.domain.model.Product
import retrofit2.Response
import retrofit2.http.GET

interface ProductApiService {

    @GET("products")
    suspend fun getProducts(): Response<List<Product>>

}