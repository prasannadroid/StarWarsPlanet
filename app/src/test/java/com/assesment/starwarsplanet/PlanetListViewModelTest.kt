package com.assesment.starwarsplanet

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.assesment.starwarsplanet.core.remote.api.APIResult
import com.assesment.starwarsplanet.features.planet.data.dto.Planet
import com.assesment.starwarsplanet.features.planet.domain.usecase.PlanetListUseCase
import com.assesment.starwarsplanet.features.planet.domain.usecase.UpdatePlanetListUseCase
import com.assesment.starwarsplanet.features.planet.presentation.viewmodels.PlanetListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class PlanetListViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var planetListUseCase: PlanetListUseCase

    @Mock
    private lateinit var updatePlanetListUseCase: UpdatePlanetListUseCase

    @Mock
    private lateinit var planetMock: Planet

    private lateinit var viewModel: PlanetListViewModel

    private val pageNumber = 1

    @Before
    fun setUp() {
        // set test dispatcher for coroutines
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = PlanetListViewModel(planetListUseCase, updatePlanetListUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadAllPlanets should update planets on success`() = runTest {
        // Given: API returns a successful response
        val successResult = APIResult.Success(planetMock)

        `when`(planetListUseCase.invoke(pageNumber)).thenReturn(flowOf(successResult))
        `when`(updatePlanetListUseCase.invoke(planetMock)).thenReturn(planetMock)

        // When: Calling loadAllPlanets()
        viewModel.loadAllPlanets()

        // Then: Verify correct flow updates
        Assert.assertEquals(planetMock, viewModel.planets.value)
        Assert.assertFalse(viewModel.isLoading.value)
        Assert.assertEquals("", viewModel.errorMessage.value)
    }

    @Test
    fun `loadAllPlanets should update errorMessage on failure`() = runTest {
        // Given: API returns an error
        val errorMessage = "Network Error"
        val errorResult = APIResult.Error(Throwable(errorMessage))

        `when`(planetListUseCase.invoke(pageNumber)).thenReturn(flowOf(errorResult))

        // When: Calling loadAllPlanets()
        viewModel.loadAllPlanets()

        // Then: Verify error message updates
        Assert.assertEquals(errorMessage, viewModel.errorMessage.value)
        Assert.assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadAllPlanets should handle exception and set error message`() = runTest {
        // Given: API call throws an exception
        val exceptionMessage = "Unexpected Error"
        `when`(planetListUseCase.invoke(pageNumber)).thenThrow(RuntimeException(exceptionMessage))

        // When: Calling loadAllPlanets()
        viewModel.loadAllPlanets()

        // Then: Ensure exception handling updates the error message
        Assert.assertEquals(exceptionMessage, viewModel.errorMessage.value)
        Assert.assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `getPlanet should return a Planet with default values`() {
        // Arrange: Initialize the ViewModel
        val viewModel = PlanetListViewModel(planetListUseCase = mock(), updatePlanetListUseCase = mock())

        // Act: Call the getPlanet() method
        val result = viewModel.getPlanet()

        // Assert: Verify the returned Planet has the default values
        assertEquals(0, result.count) // Default count should be 0
        assertTrue(result.results.isEmpty()) // Default results should be an empty list
        assertEquals("", result.next) // Default next should be an empty string
        assertEquals("", result.previous) // Default previous should be an empty string
    }
}