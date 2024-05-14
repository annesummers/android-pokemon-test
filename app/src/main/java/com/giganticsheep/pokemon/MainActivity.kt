package com.giganticsheep.pokemon

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.giganticsheep.navigation.MainNavHost
import com.giganticsheep.pokemon.ui.theme.PokemonTheme
import com.giganticsheep.ui.DisplayScreenState
import com.giganticsheep.ui.collectDisplayScreenStateAsState
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PokemonApp : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { MainContent(remember { { finish() } }) }
    }
}

@Composable
fun MainContent(
    onError: () -> Unit,
    viewModel: MainViewModel = hiltViewModel(),
) {
    PokemonTheme {
        val navHostController = rememberNavController()

        val displayState by viewModel.displayState.collectDisplayScreenStateAsState()

        Log.d("MainContent", "displayState is $displayState")
        when (displayState) {

            is DisplayScreenState.Error -> onError() // TODO

            is DisplayScreenState.Default -> MainNavHost(
                navigationGraph = remember { MainNavigationGraph(navHostController) },
                navigator = viewModel.navigator,
            )

            else -> Unit
        }
    }
}
