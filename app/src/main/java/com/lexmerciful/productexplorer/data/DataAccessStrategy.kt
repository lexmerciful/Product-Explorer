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
    networkCall: suspend () -> Resource<A>,
    saveCallResult: suspend (A) -> Unit
): Flow<Resource<T>> = flow {
    emit(Resource.loading())

    val databaseSource = databaseQuery().first()

    try {
        emit(Resource.loading(databaseSource))
        val remoteData = networkCall()
        saveCallResult(remoteData.data!!)
        emitAll(databaseQuery().map { Resource.success(it) })

    } catch (exception: Throwable) {
        Log.d("TAGII", "Error = ${exception.message}")
        emitAll(databaseQuery().map { Resource.error(exception.message ?: "Unable to reach server. Please check your connection", it) })
    }

}