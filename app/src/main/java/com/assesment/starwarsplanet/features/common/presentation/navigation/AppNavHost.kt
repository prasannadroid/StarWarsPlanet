package com.assesment.starwarsplanet.features.common.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.assesment.starwarsplanet.features.planet.presentation.screens.PlanetListScreen
import com.assesment.starwarsplanet.features.planetdetails.presentation.screens.PlanetDetailsScreen


@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = PlanetListRoute) {
        composable<PlanetListRoute> {
            PlanetListScreen(navController)
        }
        composable(
            route = "detail/{planetName}/{orbitalPeriod}/{gravity}/{index}",
            arguments = listOf(
                navArgument("planetName") { type = NavType.StringType },
                navArgument("orbitalPeriod") { type = NavType.StringType },
                navArgument("gravity") { type = NavType.StringType },
                navArgument("index") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val planetName = backStackEntry.arguments?.getString("planetName") ?: "Unknown"
            val orbitalPeriod = backStackEntry.arguments?.getString("orbitalPeriod") ?: "Unknown"
            val gravity = backStackEntry.arguments?.getString("gravity") ?: "Unknown"
            val index = backStackEntry.arguments?.getInt("index") ?: 0

            PlanetDetailsScreen(navController, planetName, orbitalPeriod, gravity, index)
        }

    }
}