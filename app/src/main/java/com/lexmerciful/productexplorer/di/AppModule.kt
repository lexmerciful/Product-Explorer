package com.lexmerciful.productexplorer.di

import android.content.Context
import androidx.room.Room
import com.lexmerciful.productexplorer.data.local.ProductDao
import com.lexmerciful.productexplorer.data.local.ProductDatabase
import com.lexmerciful.productexplorer.data.remote.ProductApiService
import com.lexmerciful.productexplorer.common.Constants.BASE_URL
import com.lexmerciful.productexplorer.common.Constants.DB_NAME
import com.lexmerciful.productexplorer.data.repository.ProductRepositoryImpl
import com.lexmerciful.productexplorer.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideProductDatabase(
        @ApplicationContext context: Context
    ): ProductDatabase {
        return Room.databaseBuilder(context, ProductDatabase::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideProductDao(
        productDatabase: ProductDatabase
    ): ProductDao = productDatabase.productDao()

    @Provides
    @Singleton
    fun provideProductApiService(): ProductApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideProductRepository(
        productApiService: ProductApiService,
        productDao: ProductDao
    ): ProductRepository {
        return ProductRepositoryImpl(productApiService, productDao)
    }
}