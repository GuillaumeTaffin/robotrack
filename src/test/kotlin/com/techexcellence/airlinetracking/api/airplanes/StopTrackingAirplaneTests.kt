package com.techexcellence.airlinetracking.api.airplanes

import com.techexcellence.airlinetracking.api.BaseApiTests
import com.techexcellence.airlinetracking.utils.airplaineIsTracked
import io.restassured.module.webtestclient.RestAssuredWebTestClient.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient

class StopTrackingAirplaneTests(@Autowired webTestClient: WebTestClient) : BaseApiTests(webTestClient) {

    @Test
    fun `Airplane info cannot be found after it is not tracked anymore`() {
        val ruddy = given().airplaineIsTracked("Ruddy")

        delete("airplanes/${ruddy.id}")
            .then()
            .status(HttpStatus.OK)

        get("airplanes/${ruddy.id}")
            .then()
            .status(HttpStatus.NOT_FOUND)
    }
    
}