package com.techexcellence.airlinetracking.airplanes

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.error
import reactor.kotlin.core.publisher.switchIfEmpty
import java.util.*

@RestController
@RequestMapping("/airplanes")
class AirplanesController(
    @Autowired val moveNotifications: MoveNotifications,
    private val airplaneService: AirplaneService,
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun trackAirplane(@RequestBody dto: AirplaneDto): Mono<AirplaneDto> = airplaneService.trackAirplane(dto)

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun findAirplane(@PathVariable("id") id: Int): Mono<AirplaneDto> = airplaneService.findAirplane(id)

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun findAllAirplanes(): Flux<AirplaneDto> = airplaneService.findAllAirplanes()

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun stopTrackingAirplane(@PathVariable("id") id: Int): Mono<Void> = airplaneService.stropTrackingAirplane(id)

    @PostMapping("/{id}/move")
    fun moveAirplane(@RequestBody moveDto: MoveDto, @PathVariable id: Int): Mono<AirplaneDto> =
        airplaneService
            .moveAirplane(id, moveDto)

}

@Service
class AirplaneService(
    private val registry: AirplanesRegistry,
    private val notificationsService: NotificationsService
) {

    fun trackAirplane(airplaneDto: AirplaneDto): Mono<AirplaneDto> =
        registry
            .save(dtoToRecord(airplaneDto))
            .map { recordToDto(it) }

    fun findAirplane(id: Int): Mono<AirplaneDto> =
        registry
            .findById(id)
            .map { recordToDto(it) }
            .switchIfEmpty { error(AirplaneNotFoundException()) }

    fun findAllAirplanes(): Flux<AirplaneDto> =
        registry
            .findAll()
            .map { recordToDto(it) }

    fun stropTrackingAirplane(id: Int): Mono<Void> = registry.deleteById(id)

    fun moveAirplane(id: Int, moveDto: MoveDto): Mono<AirplaneDto> =
        registry
            .findById(id)
            .map { it.applyMove(moveDto) }
            .flatMap { registry.save(it) }
            .map { recordToDto(it) }
            .doOnNext { notificationsService.sendMoveNotification(it) }

    private fun AirplaneRecord.applyMove(moveDto: MoveDto) = this.copy(
        latitude = this.latitude + moveDto.latitude,
        longitude = this.longitude + moveDto.longitude
    )

}

data class MoveDto(
    val latitude: Int,
    val longitude: Int
)

data class AirplaneDto(
    val id: Int? = null,
    val name: String,
    val latitude: Int = 0,
    val longitude: Int = 0
)

@Table("airplanes")
data class AirplaneRecord(
    @Id val id: Int?,
    val name: String,
    val latitude: Int,
    val longitude: Int
)

fun dtoToRecord(dto: AirplaneDto) = AirplaneRecord(
    id = dto.id,
    name = dto.name,
    latitude = dto.latitude,
    longitude = dto.longitude
)

fun recordToDto(dto: AirplaneRecord) = AirplaneDto(
    id = dto.id,
    name = dto.name,
    latitude = dto.latitude,
    longitude = dto.longitude
)

@Repository
interface AirplanesRegistry : ReactiveCrudRepository<AirplaneRecord, Int>

@ResponseStatus(HttpStatus.NOT_FOUND)
class AirplaneNotFoundException : Exception()