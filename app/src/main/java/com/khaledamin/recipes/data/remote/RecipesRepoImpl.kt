package com.khaledamin.recipes.data.remote

import coil.network.HttpException
import com.khaledamin.recipes.data.model.Recipe
import com.khaledamin.recipes.utils.ResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException

class RecipesRepoImpl(
    private val api: RecipesApi,
) : RecipesRepo {
    override fun getRecipesList(): Flow<ResultState<List<Recipe>>> {
        return flow {
            val recipes = try {
                api.getRecipeResponse()
            } catch (e: IOException) {
                e.printStackTrace()
                emit(ResultState.Error(message = e.message))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(ResultState.Error(message = e.message))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(ResultState.Error(message = e.message))
                return@flow
            }
            emit(ResultState.Success(data = recipes.recipes))
        }
    }
}