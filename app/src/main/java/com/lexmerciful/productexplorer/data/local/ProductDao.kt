package com.lexmerciful.productexplorer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lexmerciful.productexplorer.domain.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProduct(products: List<Product>)

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: Int): Flow<Product>

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    @Query("DELETE FROM products")
    fun deleteAllProduct()

}