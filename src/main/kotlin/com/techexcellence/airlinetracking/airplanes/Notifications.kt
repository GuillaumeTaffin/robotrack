package com.techexcellence.airlinetracking.airplanes

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
    fun moveNotifications(): Flux<ServerSentEvent<AirplaneDto>> = Flux.concat(
        connectionStartEvent(),
        notificationsService.moveEvents()
    )

    private fun connectionStartEvent(): Flux<ServerSentEvent<AirplaneDto>> = Flux.just(
        sse {
            event("airplane-move-connected")
        }
    )

}

@Service
class NotificationsService {
    private val eventSink: Many<AirplaneDto> = Sinks.many().multicast().onBackpressureBuffer()

    fun sendMoveNotification(airplaneDto: AirplaneDto) =
        eventSink.tryEmitNext(airplaneDto)

    fun moveEvents(): Flux<ServerSentEvent<AirplaneDto>> =
        eventSink
            .asFlux()
            .map { wrapMoveInNotification(it) }

    private fun wrapMoveInNotification(it: AirplaneDto) =
        sse {
            event("airplane-move")
            data(it)
        }

}

fun <T> sse(bloc: ServerSentEvent.Builder<T>.() -> Unit): ServerSentEvent<T> {
    val builder = ServerSentEvent.builder<T>()
    builder.bloc()
    return builder.build()
}
