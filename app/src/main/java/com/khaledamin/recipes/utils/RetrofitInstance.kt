package com.khaledamin.recipes.utils

import com.khaledamin.recipes.data.remote.RecipesApi
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
    val api: RecipesApi = Retrofit.Builder().baseUrl(RecipesApi.BASE_URL).addConverterFactory(GsonConverterFactory.create())
        .client(client).build().create(RecipesApi::class.java)
}