package com.gt.robotrack.robots

import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import reactor.core.publisher.Sinks.Many


@RestController
@RequestMapping("/notifications/move")
class MoveNotifications {

    private val eventSink: Many<RobotDto> = Sinks.many().multicast().onBackpressureBuffer()

    @GetMapping
    fun moveNotifications(): Flux<ServerSentEvent<RobotDto>> {
        return Flux.concat(
            Flux.just(
                sse {
                    event("robot-move-connected")
                }
            ),
            eventSink.asFlux().map {
                sse {
                    event("robot-move")
                    data(it)
                }
            }
        )

    }

    fun sendMoveNotification(robotDto: RobotDto) {
        eventSink.tryEmitNext(robotDto)
    }

}

fun <T> sse(bloc: ServerSentEvent.Builder<T>.() -> Unit): ServerSentEvent<T> {
    val builder = ServerSentEvent.builder<T>()
    builder.bloc()
    return builder.build()
}
