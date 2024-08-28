package com.khaledamin.recipes.utils



sealed class ResultState<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T?) : ResultState<T>(data)
    class Error<T>(data: T? = null, message: String?) : ResultState<T>(data,message)
}