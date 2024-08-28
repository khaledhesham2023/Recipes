package com.khaledamin.recipes.data.remote

import com.khaledamin.recipes.data.model.Recipe
import com.khaledamin.recipes.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface RecipesRepo {
    fun getRecipesList(): Flow<ResultState<List<Recipe>>>
}