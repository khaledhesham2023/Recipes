package com.khaledamin.recipes.data.remote

import com.khaledamin.recipes.data.model.RecipesResponse
import retrofit2.http.GET

interface RecipesApi {
    @GET("recipes")
    suspend fun getRecipeResponse(): RecipesResponse

    companion object {
        const val BASE_URL = "https://dummyjson.com/"
    }
}