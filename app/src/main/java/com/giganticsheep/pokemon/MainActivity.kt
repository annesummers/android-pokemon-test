package com.giganticsheep.pokemon

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.giganticsheep.navigation.MainNavHost
import com.giganticsheep.pokemon.ui.theme.PokemonTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PokemonApp : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { MainContent() }
    }
}

@Composable
fun MainContent(
    viewModel: MainViewModel = hiltViewModel(),
) {
    PokemonTheme {
        val navHostController = rememberNavController()

        MainNavHost(
            navigationGraph = remember { MainNavigationGraph(navHostController) },
            navigator = viewModel.navigator,
        )
    }
}
