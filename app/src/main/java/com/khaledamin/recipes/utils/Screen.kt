package com.khaledamin.recipes.utils

sealed class Screen(val route: String) {
    data object HomeScreen : Screen("homeFragment")
    data object RecipeScreen :  Screen("recipeFragment")

    fun withArgs(vararg args: String): String{
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}