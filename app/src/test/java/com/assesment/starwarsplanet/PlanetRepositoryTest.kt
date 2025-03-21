package com.assesment.starwarsplanet

import app.cash.turbine.test
import com.assesment.starwarsplanet.core.local.PlanetDao
import com.assesment.starwarsplanet.core.local.PlanetEntity
import com.assesment.starwarsplanet.core.local.PlanetWithResults
import com.assesment.starwarsplanet.core.remote.api.APIResult
import com.assesment.starwarsplanet.core.remote.api.APIService
import com.assesment.starwarsplanet.core.remote.response.PlanetResponse
import com.assesment.starwarsplanet.core.services.NetworkService
import com.assesment.starwarsplanet.features.planet.data.repository.PlanetRepositoryImpl
import com.assesment.starwarsplanet.features.planet.data.dto.Planet
import com.assesment.starwarsplanet.features.planet.data.mapper.ResponseMapper
import com.assesment.starwarsplanet.features.planet.domain.repository.PlanetRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response

class PlanetRepositoryTest {

    private lateinit var closeable: AutoCloseable

    // Mock dependencies
    private lateinit var planetRepository: PlanetRepositoryImpl

    @Mock
    private lateinit var planetResponse: PlanetResponse

    @Mock
    private lateinit var localDaoService: PlanetDao

    @Mock
    private lateinit var planet: Planet

    @Mock
    private lateinit var apiService: APIService

    @Mock
    private lateinit var responseMapper: ResponseMapper

    @Mock
    private lateinit var networkService: NetworkService

    private val pageNumber = 1

    @Before
    fun setUp() {
        // Initialize mocks before running tests
        closeable = MockitoAnnotations.openMocks(this)
        planetRepository =
            PlanetRepositoryImpl(apiService, localDaoService, responseMapper, networkService)
    }

    @After
    fun tearDown() {
        // Close mock resources after test execution
        closeable.close()
    }

    @Test
    fun `getAllPlanets should emit APIResult Success when API call is successful and mapping succeeds`() =
        runTest {
            // Arrange: Mock API response and successful mapping
            val apiResponse = Response.success(planetResponse)

            `when`(apiService.getAllPlanets(pageNumber)).thenReturn(apiResponse)
            `when`(responseMapper.mapToPlanet(planetResponse)).thenReturn(planet)

            // Act: Call the method under test
            val resultFlow = planetRepository.getRemotePlanets(pageNumber)

            // Assert: Verify the success result and emitted data
            resultFlow.test {
                val result = awaitItem()
                assertTrue(result is APIResult.Success)
                assertEquals(planet, (result as APIResult.Success).data)
                awaitComplete()
            }
        }

    @Test
    fun `getAllPlanets should emit APIResult Error when API call returns an error response`() =
        runTest {
            // Arrange: Mock API response with an error
            val errorMessage = "Not Found"
            val apiResponse =
                Response.error<PlanetResponse>(404, errorMessage.toResponseBody(null))

            `when`(apiService.getAllPlanets(pageNumber)).thenReturn(apiResponse)

            // Act: Call the method under test
            val resultFlow = planetRepository.getRemotePlanets(pageNumber)

            // Assert: Verify that an error result is emitted
            resultFlow.test {
                val result = awaitItem()
                assertTrue(result is APIResult.Error)
                assertEquals("Planets Not Found", (result as APIResult.Error).exception.message)
                awaitComplete()
            }
        }

    @Test
    fun `getAllPlanets should emit APIResult Error when mapping fails`() = runTest {

        // Arrange: Mock successful API response but mapping failure
        val apiResponse = Response.success(planetResponse)

        `when`(apiService.getAllPlanets(pageNumber)).thenReturn(apiResponse)
        `when`(responseMapper.mapToPlanet(planetResponse)).thenReturn(null)

        // Act: Call the method under test
        val resultFlow = planetRepository.getRemotePlanets(pageNumber)

        // Assert: Verify that a mapping error result is emitted
        resultFlow.test {
            val result = awaitItem()
            assertTrue(result is APIResult.Error)
            assertEquals(
                "Failed to map API response to Planet object",
                (result as APIResult.Error).exception.message
            )
            awaitComplete()
        }
    }

    @Test
    fun `getAllPlanets should emit APIResult Error when API call throws an exception`() = runTest {

        // Arrange: Simulate an exception during API call
        val exception = RuntimeException("Network failure")
        `when`(apiService.getAllPlanets(pageNumber)).thenThrow(exception)

        // Act: Call the method under test
        val result = planetRepository.getRemotePlanets(pageNumber).toList()

        // Assert: Verify that an error result is emitted
        assert(result.first() is APIResult.Error)
        val errorResult = result.first() as APIResult.Error
        assertEquals("Network failure", errorResult.exception.message)
    }

    @Test
    fun `getPlanets should return cached data when offline and cache exists`() = runTest {
        // Arrange: Simulate offline state by mocking the network service to return false
        `when`(networkService.isInternetAvailable()).thenReturn(false)

        // Mock the cached planet data available in the local database
        val cachedPlanet = PlanetWithResults(PlanetEntity(1, 0, "", ""), emptyList())

        // Mock the database call to return the cached planet data
        `when`(localDaoService.getPlanetWithResults()).thenReturn(cachedPlanet)

        // Mock the response mapper to map the cached data into a `Planet` object
        `when`(responseMapper.getPlanetFromEntity(cachedPlanet)).thenReturn(
            Planet(1, "", "", emptyList())
        )

        // Act: Call the `getPlanets` method to simulate fetching planets
        val result = planetRepository.getPlanets(1).toList()

        // Assert: Ensure the result is a success and the data is fetched from the cache
        assertTrue(result.first() is APIResult.Success)
    }

    @Test
    fun `getPlanets should return error when offline and no cache available`() = runTest {
        // Arrange: Simulate offline state by mocking the network service to return false
        `when`(networkService.isInternetAvailable()).thenReturn(false)

        // Mock the scenario where there is no cached data available
        `when`(localDaoService.getPlanetWithResults()).thenReturn(null)

        // Act: Call the `getPlanets` method when offline and no cache
        val result = planetRepository.getPlanets(1).toList()

        // Assert: Ensure the result is an error because no cache is available
        assertTrue(result.first() is APIResult.Error)
    }

    @Test
    fun `getPlanets should return error when requesting next page and offline`() = runTest {
        // Arrange: Simulate offline state by mocking the network service to return false
        `when`(networkService.isInternetAvailable()).thenReturn(false)

        // Act: Call the `getPlanets` method for a subsequent page while offline
        val result = planetRepository.getPlanets(2).toList()

        // Assert: Ensure the result is an error because next page is requested while offline
        assertTrue(result.first() is APIResult.Error)
    }

}
