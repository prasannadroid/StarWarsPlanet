package com.assesment.starwarsplanet.features.planet.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.assesment.starwarsplanet.R
import com.assesment.starwarsplanet.features.planet.data.dto.PlanetResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanetItem(planetResult: PlanetResult, index: Int, onItemClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
    ) {
        Card(
            onClick = {
                onItemClick()
            },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            modifier =
            Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(start = 15.dp, top = 15.dp, end = 15.dp),
            shape = RoundedCornerShape(15.dp)
        ) {
            // image holder composable
            PlanetImage(index = index)
            // planet name
            Text(
                modifier = Modifier.padding(start = 15.dp, top = 10.dp),
                text = planetResult.name,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            // climate
            Text(
                modifier = Modifier.padding(start = 15.dp, top = 6.dp),
                text = planetResult.climate,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
fun PlanetImage(index: Int) {
    // make image section in the card view
    AsyncImage(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(15.dp)),
        model = ImageRequest.Builder(LocalContext.current)
            .data("https://picsum.photos/300/200?random=$index")
            .crossfade(true)
            .build(),
        contentDescription = stringResource(id = R.string.planet_image),
        contentScale = ContentScale.Crop,
        placeholder = painterResource(id = R.drawable.ic_place_holder),
        error = painterResource(id = R.drawable.ic_not_available)
    )
}
