package com.assesment.starwarsplanet.features.planet.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assesment.starwarsplanet.core.remote.api.APIResult
import com.assesment.starwarsplanet.features.planet.data.dto.Planet
import com.assesment.starwarsplanet.features.planet.domain.usecase.PlanetListUseCase
import com.assesment.starwarsplanet.features.planet.domain.usecase.UpdatePlanetListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing and providing planet data to the UI.
 * It handles data fetching, state management, and error handling.
 *
 * @param planetListUseCase Use case for fetching planet data from the repository.
 * @param updatePlanetListUseCase Use case for updating and accumulating planet list results.
 */
@HiltViewModel
class PlanetListViewModel @Inject constructor(
    private val planetListUseCase: PlanetListUseCase,
    private val updatePlanetListUseCase: UpdatePlanetListUseCase
) : ViewModel() {

    // Keeps track of the current page number for pagination
    private var pageNumber = 1

    // Holds the planet data as a state flow
    private val _planets = MutableStateFlow(getPlanet())
    val planets: StateFlow<Planet?> = _planets

    // Loading state to indicate data fetching progress
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Holds error messages in case of failures
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    /**
     * Fetches all planets from the API or local database.
     * Uses pagination to retrieve data incrementally.
     */
    fun loadAllPlanets() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                planetListUseCase(pageNumber)?.collect {
                    _isLoading.value = false
                    when (it) {
                        // Success: Update planet data and increment page number
                        is APIResult.Success -> {
                            val planet = updatePlanetListUseCase(it.data)
                            _planets.value = planet
                            pageNumber++
                        }
                        // Error: Capture error message
                        is APIResult.Error -> {
                            _errorMessage.value = it.exception.message ?: "Unknown Error"
                        }
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.message ?: "Unknown Error"
            }
        }
    }

    /**
     * Provides an empty planet object as the initial state.
     *
     * @return A [Planet] object with default values.
     */
    fun getPlanet() = Planet(count = 0, results = emptyList(), next = "", previous = "")
}
