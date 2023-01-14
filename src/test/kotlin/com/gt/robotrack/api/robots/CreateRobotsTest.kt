package com.gt.robotrack.api.robots

import com.gt.robotrack.api.BaseApiTests
import com.gt.robotrack.robots.RobotDto
import com.gt.robotrack.utils.extractBodyAsRobot
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.restassured.module.webtestclient.RestAssuredWebTestClient.given
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient

class CreateRobotsTest(@Autowired webTestClient: WebTestClient) : BaseApiTests(webTestClient) {

    @Test
    fun `Should allow to create a new robot and give back its info`() {
        val createdRobot = given()
            .body(RobotDto(name = "Ted"))
            .post("/robots")
            .then()
            .status(HttpStatus.CREATED)
            .extractBodyAsRobot()

        with(createdRobot) {
            id shouldNotBe null
            name shouldBe "Ted"
            latitude shouldBe 0
            longitude shouldBe 0
        }
    }

    @Test
    fun `Should allow to choose the creation coordinates`() {
        val bob = given()
            .body(
                RobotDto(
                    name = "Bob",
                    latitude = -45,
                    longitude = 166
                )
            )
            .post("/robots")
            .then()
            .status(HttpStatus.CREATED)
            .extractBodyAsRobot()

        with(bob) {
            id shouldNotBe null
            name shouldBe "Bob"
            latitude shouldBe -45
            longitude shouldBe 166
        }
    }

}