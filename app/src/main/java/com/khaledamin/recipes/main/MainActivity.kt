package com.khaledamin.recipes.main

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.google.gson.Gson
import com.khaledamin.recipes.data.model.Recipe
import com.khaledamin.recipes.data.remote.RecipesRepoImpl
import com.khaledamin.recipes.ui.theme.RecipesTheme
import com.khaledamin.recipes.utils.RetrofitInstance
import com.khaledamin.recipes.utils.Screen
import kotlinx.coroutines.flow.collectLatest


class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<RecipesViewModel>(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RecipesViewModel(RecipesRepoImpl(RetrofitInstance.api)) as T
            }
        }
    })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    NavigationComp()
                }
            }
        }
    }

    @Composable
    fun NavigationComp() {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Screen.HomeScreen.route
        ) {
            composable(route = Screen.HomeScreen.route) {
                HomeScreenPage(navController)
            }
            composable(
                route = Screen.RecipeScreen.route + "/{name}/{ingredients}/{instructions}/{image}",
                arguments = listOf(
                    navArgument("name") {
                        type = NavType.StringType
                    },
                    navArgument("ingredients") {
                        type = NavType.StringType
                    },
                    navArgument("instructions") {
                        type = NavType.StringType
                    },
                    navArgument("image") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name")
                val ingredients = backStackEntry.arguments?.getString("ingredients")
                val instructions = backStackEntry.arguments?.getString("instructions")
                val image = backStackEntry.arguments?.getString("image")
                RecipeScreenPage(
                    name = name!!,
                    ingredients = ingredients!!,
                    instructions = instructions!!,
                    image = image!!
                )
            }
        }
    }

    @Composable
    fun HomeScreenPage(navController: NavController) {
        val recipes = viewModel.recipes.collectAsState().value
        LaunchedEffect(key1 = viewModel.showError) {
            viewModel.showError.collectLatest {
                if (it) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error loading recipes",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        if (recipes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(all = 16.dp)
            ) {
                items(recipes.size) {
                    Recipe(image = recipes[it].image, name = recipes[it].name, Modifier.clickable {
                        navController.navigate(
                            Screen.RecipeScreen.withArgs(
                                recipes[it].name,
                                recipes[it].ingredients.joinToString(","),
                                recipes[it].instructions.joinToString(","),
                                Uri.encode(recipes[it].image)
                            )
                        )
                    })
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }

    @Composable
    fun RecipeScreenPage(name: String, ingredients: String, instructions: String, image: String) {
        val ingredientsList = ingredients.split(",")
        val instructionsList = instructions.split(",")
        val imageUrl = Uri.decode(image)
        val imageState = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current).data(imageUrl).size(Size.ORIGINAL)
                .build()
        ).state

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Image(
                    painter = imageState.painter!!,
                    contentDescription = name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Ingredients: ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

            }
            items(ingredientsList.size) {
                Text(
                    text = "${it + 1} - ${ingredientsList[it]}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(all = 8.dp)
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "How to cook: ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(instructionsList.size) {
                Text(
                    "${it + 1} - ${instructionsList[it]}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(all = 8.dp)
                )
            }
            item {
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

@Composable
fun Recipe(image: String, name: String, modifier: Modifier) {
    val imageState = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current).data(image)
            .size(Size.ORIGINAL).build()
    ).state

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .height(300.dp)
            .background(color = MaterialTheme.colorScheme.primaryContainer)
    ) {
        if (imageState is AsyncImagePainter.State.Success) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                painter = imageState.painter,
                contentDescription = name,
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(height = 6.dp))
            Text(
                text = name,
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(height = 6.dp))
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}