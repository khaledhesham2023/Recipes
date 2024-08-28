package com.khaledamin.recipes.data.model

data class RecipesResponse(
    val limit: Int,
    val recipes: List<Recipe>,
    val skip: Int,
    val total: Int
)