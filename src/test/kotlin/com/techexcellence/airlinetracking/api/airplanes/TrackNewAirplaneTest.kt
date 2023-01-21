package com.techexcellence.airlinetracking.api.airplanes

import com.techexcellence.airlinetracking.api.BaseApiTests
import com.techexcellence.airlinetracking.airplanes.AirplaneDto
import com.techexcellence.airlinetracking.utils.extractBodyAsAirplane
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.restassured.module.webtestclient.RestAssuredWebTestClient.given
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient

class TrackNewAirplaneTest(@Autowired webTestClient: WebTestClient) : BaseApiTests(webTestClient) {

    @Test
    fun `Should allow to track a new airplane and give back its info`() {
        val trackedAirplane = given()
            .body(AirplaneDto(name = "Ted"))
            .post("/airplanes")
            .then()
            .status(HttpStatus.CREATED)
            .extractBodyAsAirplane()

        with(trackedAirplane) {
            id shouldNotBe null
            name shouldBe "Ted"
            latitude shouldBe 0
            longitude shouldBe 0
        }
    }

    @Test
    fun `Should allow to choose the initial location of the airplane`() {
        val bob = given()
            .body(
                AirplaneDto(
                    name = "Bob",
                    latitude = -45,
                    longitude = 166
                )
            )
            .post("/airplanes")
            .then()
            .status(HttpStatus.CREATED)
            .extractBodyAsAirplane()

        with(bob) {
            id shouldNotBe null
            name shouldBe "Bob"
            latitude shouldBe -45
            longitude shouldBe 166
        }
    }

}