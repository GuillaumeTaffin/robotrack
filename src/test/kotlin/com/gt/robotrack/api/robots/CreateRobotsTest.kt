package com.gt.robotrack.api.robots

import com.gt.robotrack.api.BaseApiTests
import com.gt.robotrack.robots.RobotDto
import com.gt.robotrack.utils.extractBodyAsRobot
import io.restassured.module.webtestclient.RestAssuredWebTestClient.given
import org.assertj.core.api.Assertions.assertThat
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
            assertThat(id).isNotNull
            assertThat(name).isEqualTo("Ted")
            assertThat(latitude).isZero
            assertThat(longitude).isZero
        }
    }

}