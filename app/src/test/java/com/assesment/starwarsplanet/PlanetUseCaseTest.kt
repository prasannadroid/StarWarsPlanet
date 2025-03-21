package com.assesment.starwarsplanet

import app.cash.turbine.test
import com.assesment.starwarsplanet.TestHelper.Companion.createPlanet
import com.assesment.starwarsplanet.core.remote.api.APIResult
import com.assesment.starwarsplanet.features.planet.data.dto.Planet
import com.assesment.starwarsplanet.features.planet.domain.repository.PlanetRepository
import com.assesment.starwarsplanet.features.planet.domain.usecase.PlanetListUseCase
import com.assesment.starwarsplanet.features.planet.domain.usecase.UpdatePlanetListUseCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations


class PlanetUseCaseTest {

    // Use case instances
    private lateinit var updatePlanetListUseCase: UpdatePlanetListUseCase
    private lateinit var planetListUseCase: PlanetListUseCase

    // AutoCloseable instance to release resources
    private lateinit var closeable: AutoCloseable

    // Mock dependencies
    @Mock
    private lateinit var planetRepository: PlanetRepository

    @Before
    fun setUp() {
        // Initialize Mockito mocks before each test
        closeable = MockitoAnnotations.openMocks(this)
        updatePlanetListUseCase = UpdatePlanetListUseCase()
        planetListUseCase = PlanetListUseCase(planetRepository)
    }

    @After
    fun tearDown() {
        // Close the mocks after each test
        closeable.close()
    }

    @Test
    fun `invoke should get planet results successfully`() = runTest {
        // Arrange: Setup test data and mock API response
        val pageNumber = 1
        val planet =
            Planet(count = 0, results = listOf(createPlanet("Earth")), next = "", previous = "")
        val apiResult = APIResult.Success(planet)

        // Mock the repository response
        `when`(planetRepository.getRemotePlanets(pageNumber)).thenReturn(flowOf(apiResult))

        // Act: Call the use case
        val resultFlow = planetListUseCase.invoke(pageNumber)

        // Assert: Verify that the API call returns a success response
        resultFlow?.test {
            val result = awaitItem()
            assertTrue(result is APIResult.Success)
            assertEquals(planet, (result as APIResult.Success).data)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should get planet Error`() = runTest {
        // Arrange: Setup test data and mock API error response
        val pageNumber = 1
        val apiResult = APIResult.Error(Throwable(""))

        // Mock the repository response with an error
        `when`(planetRepository.getRemotePlanets(pageNumber)).thenReturn(flowOf(apiResult))

        // Act: Call the use case within a coroutine scope
        runBlocking {
            val resultFlow = planetListUseCase.invoke(pageNumber)

            // Assert: Verify that an error result is emitted
            resultFlow?.test {
                val result = awaitItem()
                assertTrue(result is APIResult.Error)
                awaitComplete()
            }
        }
    }

    @Test
    fun `invoke should update planet results successfully`() {
        // Arrange: Setup initial planet data
        val initialResults = listOf(createPlanet("Earth"), createPlanet("Mars"))
        val planet =
            Planet(results = initialResults.toMutableList(), count = 0, next = "", previous = "")

        // Act: Call the use case to update the planet list
        val updatedPlanet1 = updatePlanetListUseCase.invoke(planet)

        // Assert: Check if the initial list has the correct size
        assertEquals(2, updatedPlanet1.results.size) // Initially 2 planets

        // Arrange: Add new planets
        val newResults = listOf(createPlanet("Jupiter"), createPlanet("Saturn"))
        val planet2 =
            Planet(results = newResults.toMutableList(), count = 0, next = "", previous = "")

        // Act: Call the use case again with new data
        val updatedPlanet2 = updatePlanetListUseCase.invoke(planet2)

        // Assert: Check if the updated list contains all planets
        assertEquals(4, updatedPlanet2.results.size) // Previous 2 + New 2 = 4

        // Assert: Verify that planet names are in expected order
        assertEquals(
            listOf("Earth", "Mars", "Jupiter", "Saturn"),
            updatedPlanet2.results.map { it.name }
        )
    }
}
