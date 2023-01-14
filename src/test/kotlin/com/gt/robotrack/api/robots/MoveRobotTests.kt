package com.gt.robotrack.api.robots

import com.gt.robotrack.api.BaseApiTests
import com.gt.robotrack.robots.MoveDto
import com.gt.robotrack.robots.RobotDto
import com.gt.robotrack.utils.extractBodyAsRobot
import com.gt.robotrack.utils.robotIsCreated
import io.restassured.module.webtestclient.RestAssuredWebTestClient.given
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.Duration


class MoveRobotTests(
    @Autowired webTestClient: WebTestClient
) : BaseApiTests(webTestClient) {

    @Test
    fun `Should provide new robot coordinates when a move command is sent`() {
        val piper = given().robotIsCreated(name = "piper", latitude = -3, longitude = 92)

        val notificationFeed = connectNotificationFeed()

        val piperAfterMoving = given()
            .body(
                MoveDto(
                    latitude = 56,
                    longitude = -24
                )
            )
            .post("robots/${piper.id}/move")
            .then()
            .status(HttpStatus.OK)
            .extractBodyAsRobot()

        with(piperAfterMoving) {
            assertThat(id).isEqualTo(piper.id)
            assertThat(name).isEqualTo(piper.name)
            assertThat(latitude).isEqualTo(53)
            assertThat(longitude).isEqualTo(68)
        }

        verifyOn(notificationFeed) {
            consumeNextWith {
                assertThat(it.event()).isEqualTo("robot-move-connected")
            }
            consumeNextWith {
                assertThat(it.event()).isEqualTo("robot-move")
                assertThat(it.data()).isEqualTo(piperAfterMoving)
            }
        }
    }

    private fun connectNotificationFeed() = webTestClient.get()
        .uri("/notifications/move")
        .accept(MediaType.TEXT_EVENT_STREAM)
        .exchange()
        .expectStatus().isOk
        .returnResult(typeReference<ServerSentEvent<RobotDto>>())
        .responseBody

}

fun <T> verifyOn(flux: Flux<T>, block: StepVerifier.FirstStep<T>.() -> Unit): Duration =
    StepVerifier
        .create(flux)
        .apply(block)
        .thenCancel()
        .verify()


inline fun <reified T> typeReference() = object : ParameterizedTypeReference<T>() {}
