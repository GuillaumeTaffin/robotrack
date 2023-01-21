package com.techexcellence.airlinetracking.api.airplanes

import com.techexcellence.airlinetracking.api.BaseApiTests
import com.techexcellence.airlinetracking.airplanes.MoveDto
import com.techexcellence.airlinetracking.airplanes.AirplaneDto
import com.techexcellence.airlinetracking.utils.extractBodyAsAirplane
import com.techexcellence.airlinetracking.utils.airplaineIsTracked
import io.kotest.matchers.shouldBe
import io.restassured.module.webtestclient.RestAssuredWebTestClient.given
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


class MoveAirplaneTests(
    @Autowired webTestClient: WebTestClient
) : BaseApiTests(webTestClient) {

    @Test
    fun `Should provide new airplane coordinates when a move command is sent`() {
        val piper = given().airplaineIsTracked(name = "piper", latitude = -3, longitude = 92)

        val notificationFeed = connectNotificationFeed()

        val piperAfterMoving = given()
            .body(
                MoveDto(
                    latitude = 56,
                    longitude = -24
                )
            )
            .post("airplanes/${piper.id}/move")
            .then()
            .status(HttpStatus.OK)
            .extractBodyAsAirplane()

        with(piperAfterMoving) {
            id shouldBe piper.id
            name shouldBe piper.name
            latitude shouldBe 53
            longitude shouldBe 68
        }

        verifyOn(notificationFeed) {
            consumeNextWith {
                it.event() shouldBe "airplane-move-connected"
            }
            consumeNextWith {
                it.event() shouldBe "airplane-move"
                it.data() shouldBe piperAfterMoving
            }
        }
    }

    private fun connectNotificationFeed() = webTestClient.get()
        .uri("/notifications/move")
        .accept(MediaType.TEXT_EVENT_STREAM)
        .exchange()
        .expectStatus().isOk
        .returnResult(typeReference<ServerSentEvent<AirplaneDto>>())
        .responseBody

}

fun <T> verifyOn(flux: Flux<T>, block: StepVerifier.FirstStep<T>.() -> Unit): Duration =
    StepVerifier
        .create(flux)
        .apply(block)
        .thenCancel()
        .verify()


inline fun <reified T> typeReference() = object : ParameterizedTypeReference<T>() {}
