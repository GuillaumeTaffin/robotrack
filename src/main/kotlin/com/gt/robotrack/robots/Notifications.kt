package com.gt.robotrack.robots

import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import reactor.core.publisher.Sinks.Many


@RestController
@RequestMapping("/notifications/move")
class MoveNotifications(
    private val notificationsService: NotificationsService
) {

    @GetMapping
    fun moveNotifications(): Flux<ServerSentEvent<RobotDto>> = Flux.concat(
        connectionStartEvent(),
        notificationsService.moveEvents()
    )

    private fun connectionStartEvent(): Flux<ServerSentEvent<RobotDto>> = Flux.just(
        sse {
            event("robot-move-connected")
        }
    )

}

@Service
class NotificationsService {
    private val eventSink: Many<RobotDto> = Sinks.many().multicast().onBackpressureBuffer()

    fun sendMoveNotification(robotDto: RobotDto) =
        eventSink.tryEmitNext(robotDto)

    fun moveEvents(): Flux<ServerSentEvent<RobotDto>> =
        eventSink
            .asFlux()
            .map { wrapMoveInNotification(it) }

    private fun wrapMoveInNotification(it: RobotDto) =
        sse {
            event("robot-move")
            data(it)
        }

}

fun <T> sse(bloc: ServerSentEvent.Builder<T>.() -> Unit): ServerSentEvent<T> {
    val builder = ServerSentEvent.builder<T>()
    builder.bloc()
    return builder.build()
}
