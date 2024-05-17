package com.giganticsheep.pokemon.ui.common

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.giganticsheep.pokemon.domain.pokemon.model.PokemonDisplay
import com.giganticsheep.pokemon.ui.theme.Padding
import com.giganticsheep.pokemon.ui.theme.customColor1Dark

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PokemonTopAppBar(
    title: String,
    onUpClicked: (() -> Unit)? = null,
) {
    TopAppBar(
        navigationIcon = {
            onUpClicked
                ?.let { upClicked ->
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null, // decorative element
                        modifier = Modifier.clickable { upClicked() },
                    )
                }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = customColor1Dark,
            )
        },
    )
}

@Composable
fun PokemonImage(
    pokemon: PokemonDisplay,
    onPokemonClicked: ((Int) -> Unit)? = null,
) {
    Log.d("Image", pokemon.imageUrl)

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .padding(Padding.itemPadding)
            .background(MaterialTheme.colorScheme.onPrimary)
            .fillMaxWidth(),
    ) {
        AsyncImage(
            modifier = (
                onPokemonClicked
                    ?.let { Modifier.clickable { onPokemonClicked(pokemon.id) } }
                    ?: Modifier
                )
                .fillMaxWidth()
                .padding(3.dp)
                .background(MaterialTheme.colorScheme.inverseSurface)
                .padding(Padding.itemPadding)
                .aspectRatio(1F),
            model = ImageRequest.Builder(LocalContext.current)
                .data(pokemon.imageUrl)
                .decoderFactory(SvgDecoder.Factory())
                .crossfade(true)
                .build(),
            contentDescription = pokemon.name,
            contentScale = ContentScale.Fit,
        )
    }
}
