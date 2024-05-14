package com.giganticsheep.pokemon.splash

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.giganticsheep.navigation.NonNestedNavigationGraph
import com.giganticsheep.pokemon.ui.theme.HeadlineLargeText
import com.giganticsheep.pokemon.ui.theme.PokemonTheme


@Composable
fun SplashScreen(graph: NonNestedNavigationGraph) {
    SplashScreenContent()
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SplashScreenContent() {
    Scaffold(modifier = Modifier) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            HeadlineLargeText("SPLASH")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    PokemonTheme {
        SplashScreenContent()
    }
}
