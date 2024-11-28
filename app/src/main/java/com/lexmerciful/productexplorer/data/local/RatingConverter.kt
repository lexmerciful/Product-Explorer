package com.lexmerciful.productexplorer.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.lexmerciful.productexplorer.domain.model.Rating

class RatingConverter {
    val gson = Gson()

    @TypeConverter
    fun ratingToString(rating: Rating?): String {
        return if (rating != null) {
            gson.toJson(rating)
        } else {
            ""
        }
    }

    @TypeConverter
    fun stringToRating(ratingString: String?): Rating? {
        return if (ratingString != null) {
            gson.fromJson(ratingString, Rating::class.java)
        } else {
            null
        }
    }

}