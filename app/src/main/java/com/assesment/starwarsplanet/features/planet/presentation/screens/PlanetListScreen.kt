package com.assesment.starwarsplanet.features.planet.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.assesment.starwarsplanet.R
import com.assesment.starwarsplanet.features.common.presentation.MyActionBar
import com.assesment.starwarsplanet.features.common.presentation.navigation.PlanetDetailsRoute
import com.assesment.starwarsplanet.features.planet.data.dto.formatValue
import com.assesment.starwarsplanet.features.planet.presentation.viewmodels.PlanetListViewModel


@Composable
fun PlanetListScreen(navController: NavController) {
    Scaffold(
        topBar = {
            MyActionBar(stringResource(id = R.string.planet_list))
        },
        content = { paddingValues ->
            // Main content of the screen
            PlanetListContainer(navController = navController, paddingValues = paddingValues)
        }
    )
}

@Composable
fun PlanetListContainer(navController: NavController, paddingValues: PaddingValues) {

    val context = LocalContext.current
    val viewModel: PlanetListViewModel = hiltViewModel()
    val planet by viewModel.planets.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val lazyListState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )

        LaunchedEffect(Unit) {
            if (planet?.count == 0) {
                viewModel.loadAllPlanets()
            }
        }

        LaunchedEffect(lazyListState) {
            snapshotFlow { lazyListState.layoutInfo }
                .collect { layoutInfo ->
                    val totalItemsFromApi = planet?.count ?: 0
                    val totalItems = layoutInfo.totalItemsCount
                    val lastVisibleItem =
                        layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

                    // Check if the user has reached the end of the list
                    if (lastVisibleItem >= totalItems - 1 && !isLoading && (totalItems < totalItemsFromApi)) {
                        viewModel.loadAllPlanets()
                    }
                }
        }

        LazyColumn(state = lazyListState) {
            // get result or empty
            val planetResult = planet?.results.orEmpty()
            // display items
            itemsIndexed(planetResult) { index, planetItems ->
                PlanetItem(planetItems, index, onItemClick = {
                    // make route
                    val route = PlanetDetailsRoute.createRoute(
                        planetItems.name.formatValue(),
                        planetItems.orbitalPeriod.formatValue(),
                        planetItems.gravity.formatValue(),
                        index
                    )
                    // navigate to planet details
                    navController.navigate(route)
                })
            }

            // Add a loading item at the end
            if (isLoading) {
                item {
                    ProgressIndicator()
                }
            }
        }

        errorMessage.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun ProgressIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}