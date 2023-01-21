package com.techexcellence.airlinetracking.api.airplanes

import com.techexcellence.airlinetracking.api.BaseApiTests
import com.techexcellence.airlinetracking.airplanes.AirplaneDto
import com.techexcellence.airlinetracking.airplanes.AirplanesRegistry
import com.techexcellence.airlinetracking.utils.extractBodyAsAirplane
import com.techexcellence.airlinetracking.utils.airplaineIsTracked
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.restassured.module.webtestclient.RestAssuredWebTestClient.get
import io.restassured.module.webtestclient.RestAssuredWebTestClient.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient

class FindAirplanesControllerTests(
    @Autowired webTestClient: WebTestClient,
    @Autowired val repository: AirplanesRegistry,
) : BaseApiTests(webTestClient) {

    @BeforeEach
    fun reset() {
        repository
            .deleteAll()
            .block()
    }

    @Test
    fun `Should be able to get  the airplane after tracking it`() {
        val tod = given().airplaineIsTracked(name = "TOD")

        val gottenAirplane = get("airplanes/${tod.id}")
            .then()
            .status(HttpStatus.OK)
            .extractBodyAsAirplane()

        gottenAirplane shouldBe tod
    }

    @Test
    fun `Should be able to get all the tracked airplanes`() {
        val airplanes = listOf("JOY", "R2D2")
            .map { name -> given().airplaineIsTracked(name) }

        val gottenAirplanes = get("airplanes")
            .then()
            .status(HttpStatus.OK)
            .extract()
            .body()
            .jsonPath()
            .getList(".", AirplaneDto::class.java)

        gottenAirplanes shouldContainAll airplanes
    }

    @Test
    fun `Should retrieve no airplanes if none are tracked`() {
        get("airplanes")
            .then()
            .status(HttpStatus.OK)
            .body(".", `is`(emptyList<AirplaneDto>()))
    }

    @Test
    fun `Should fail when trying to get an airplane that is not tracked`() {
        val id = (1000..100000).random()

        get("airplanes/$id")
            .then()
            .status(HttpStatus.NOT_FOUND)

    }

}