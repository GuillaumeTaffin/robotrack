package com.gt.robotrack.api.robots

import com.gt.robotrack.api.BaseApiTests
import com.gt.robotrack.robots.MoveDto
import com.gt.robotrack.utils.extractBodyAsRobot
import com.gt.robotrack.utils.robotIsCreated
import io.restassured.module.webtestclient.RestAssuredWebTestClient.given
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient

class MoveRobotTests(@Autowired webTestClient: WebTestClient) : BaseApiTests(webTestClient) {

    @Test
    fun `Should provide new robot coordinates when a move command is sent`() {
        val piper = given().robotIsCreated(name = "piper", latitude = -3, longitude = 92)

        val piperAfterMoving = given()
            .body(
                MoveDto(
                    latitude = 56,
                    longitude = -24
                )
            )
            .post("robots/${piper.id}/move")
            .then()
            .extractBodyAsRobot()

        with(piperAfterMoving) {
            assertThat(id).isEqualTo(piper.id)
            assertThat(name).isEqualTo(piper.name)
            assertThat(latitude).isEqualTo(53)
            assertThat(longitude).isEqualTo(68)
        }
    }
}