package com.assesment.starwarsplanet

import com.assesment.starwarsplanet.core.local.PlanetEntity
import com.assesment.starwarsplanet.core.local.PlanetResultEntity
import com.assesment.starwarsplanet.core.local.PlanetWithResults
import com.assesment.starwarsplanet.core.remote.response.PlanetResponse
import com.assesment.starwarsplanet.core.remote.response.PlanetResultResponse
import com.assesment.starwarsplanet.features.planet.data.mapper.ResponseMapper
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations


class ResponseMapperTest {

    private lateinit var closeable: AutoCloseable
    private lateinit var gson: Gson
    private lateinit var responseMapper: ResponseMapper

    @Mock
    private lateinit var planetResultResponse: PlanetResultResponse

    @Before
    fun setUp() {
        // Initialize mocks and dependencies before each test
        closeable = MockitoAnnotations.openMocks(this)
        gson = Gson()
        responseMapper = ResponseMapper(gson)
    }

    @Test
    fun `mapToPlanet should return valid Planet object when given a correct PlanetResponse`() {
        // Arrange: Given a valid PlanetResponse with mock data
        val planetResponse = PlanetResponse(
            count = 100,
            next = "next_url",
            previous = "prev_url",
            results = listOf(planetResultResponse)
        )

        // Act: When calling mapToPlanet to map the PlanetResponse to Planet
        val result = responseMapper.mapToPlanet(planetResponse)

        // Assert: Verify that the result is not null
        assertNotNull(result)
        // Assert: Verify that the number of results is correct (should be 1 as there is one planetResultResponse)
        assertEquals(1, result?.results?.size)
        // Assert: Verify that the count matches the expected value from the PlanetResponse
        assertEquals(100, result?.count)
        // Assert: Verify that the next URL is correctly mapped
        assertEquals("next_url", result?.next)
    }

    @Test
    fun `mapToPlanet should return null when Gson throws an exception`() {
        // Arrange: Given a broken Gson instance that simulates an exception
        val brokenGson = Mockito.mock(Gson::class.java)
        // Simulating a RuntimeException being thrown when attempting to convert an object to JSON
        `when`(brokenGson.toJson(Mockito.any())).thenThrow(RuntimeException("Gson error"))
        val faultyMapper = ResponseMapper(brokenGson)

        // Act: Trying to map the PlanetResponse with the broken Gson
        val result = faultyMapper.mapToPlanet(PlanetResponse(1, "next", "prev", emptyList()))

        // Assert: Verify that null is returned due to the exception thrown by Gson
        assertNull(result)
    }

    @Test
    fun `getPlanetFromEntity should correctly map PlanetWithResults to Planet`() {
        // Arrange: Create a mock PlanetWithResults object
        val planetEntity = PlanetEntity(count = 5, next = "nextPage", previous = "prevPage")
        val resultEntities = listOf(
            PlanetResultEntity(
                name = "Earth",
                orbitalPeriod = "365 days",
                climate = "Temperate",
                gravity = "9.8 m/s²"
            ),
            PlanetResultEntity(
                name = "Mars",
                orbitalPeriod = "687 days",
                climate = "Cold",
                gravity = "3.7 m/s²"
            )
        )
        val planetWithResult = PlanetWithResults(planet = planetEntity, results = resultEntities)

        // Act: Call the function
        val mappedPlanet = responseMapper.getPlanetFromEntity(planetWithResult)

        // Assert: Verify the mapping is correct
        assertEquals(5, mappedPlanet.count)
        assertEquals("nextPage", mappedPlanet.next)
        assertEquals("prevPage", mappedPlanet.previous)
        assertEquals(2, mappedPlanet.results.size)

        assertEquals("Earth", mappedPlanet.results[0].name)
        assertEquals("365 days", mappedPlanet.results[0].orbitalPeriod)
        assertEquals("Temperate", mappedPlanet.results[0].climate)
        assertEquals("9.8 m/s²", mappedPlanet.results[0].gravity)

        assertEquals("Mars", mappedPlanet.results[1].name)
        assertEquals("687 days", mappedPlanet.results[1].orbitalPeriod)
        assertEquals("Cold", mappedPlanet.results[1].climate)
        assertEquals("3.7 m/s²", mappedPlanet.results[1].gravity)
    }
}
