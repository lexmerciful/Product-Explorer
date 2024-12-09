package com.lexmerciful.productexplorer.data

import android.util.Log
import com.lexmerciful.productexplorer.common.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

fun <T, A> performGetOperation(
    databaseQuery: () -> Flow<T>,
    networkCall: suspend () -> A,
    saveCallResult: suspend (A) -> Unit,
    shouldFetch: (T) -> Boolean = { true }
): Flow<Resource<T>> = flow {
    val data = databaseQuery().first()

    if (shouldFetch(data)) {
        // Emit loading state with cached data
        emit(Resource.loading(data))
        try {
            // Network call
            val resultType = networkCall()

            // Save result to database
            saveCallResult(resultType)

            // Emit success state with the updated database data
            emitAll(databaseQuery().map { Resource.success(it) })
        } catch (throwable: Throwable) {
            println("Emitting error: ${throwable.message}, data: $data")
            emit(Resource.error(throwable.localizedMessage ?: "Unable to reach server. Please check your connection", data))
        }

    } else {
        emitAll(databaseQuery().map { Resource.success(it) })
    }
}