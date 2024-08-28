package com.khaledamin.recipes.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khaledamin.recipes.data.model.Recipe
import com.khaledamin.recipes.data.remote.RecipesRepo
import com.khaledamin.recipes.utils.ResultState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipesViewModel(private val recipesRepo: RecipesRepo): ViewModel() {

    private var _recipes: MutableStateFlow<List<Recipe>> = MutableStateFlow(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    private var _showError = Channel<Boolean>()
    val showError = _showError.receiveAsFlow()

    init {
        viewModelScope.launch {
            recipesRepo.getRecipesList().collectLatest { result ->
                when(result){
                    is ResultState.Success -> {
                        result.data.let { products ->
                            _recipes.value = products!!
                        }
                    }
                    is ResultState.Error -> {
                        _showError.send(true)
                    }
                }
            }
        }
    }
}