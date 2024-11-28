package com.lexmerciful.productexplorer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lexmerciful.productexplorer.domain.model.Product

@Database(entities = [Product::class], version = 1)
@TypeConverters(RatingConverter::class)
abstract class ProductDatabase: RoomDatabase() {

    abstract fun productDao(): ProductDao

}