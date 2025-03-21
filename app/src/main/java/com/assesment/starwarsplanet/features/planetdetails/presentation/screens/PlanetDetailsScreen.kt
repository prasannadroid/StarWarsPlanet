package com.assesment.starwarsplanet.features.planetdetails.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.assesment.starwarsplanet.R
import com.assesment.starwarsplanet.features.common.presentation.MyNavAppBar


@Composable
fun PlanetDetailsScreen(
    navController: NavController,
    planetName: String,
    orbitalPeriod: String,
    gravity: String,
    index: Int
) {
    Scaffold(
        topBar = {
            MyNavAppBar(stringResource(id = R.string.planet_details), navController)
        }, content = { paddingValues ->
            // Main content of the screen
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                PlanetCoverImage(index = index)
                Text(
                    modifier = Modifier.padding(start = 15.dp, top = 10.dp),
                    text = planetName,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    modifier = Modifier.padding(start = 15.dp, top = 6.dp),
                    text = "Orbital Period: $orbitalPeriod",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                )
                Text(
                    modifier = Modifier.padding(start = 15.dp, top = 6.dp),
                    text = "Gravity: $gravity",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                )
            }

        })
}

@Composable
fun PlanetCoverImage(index: Int) {
    AsyncImage(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
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

@Preview
@Composable
fun ItemPreview() {
    PlanetCoverImage(1)
}

