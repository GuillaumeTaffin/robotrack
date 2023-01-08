package com.gt.robotrack.robots

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.error
import reactor.kotlin.core.publisher.switchIfEmpty
import java.util.*

@RestController
@RequestMapping("/robots")
class Robots(
    @Autowired val repository: RobotsRepository
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createRobot(@RequestBody dto: RobotDto): Mono<RobotDto> {
        return repository.save(dtoToRecord(dto))
            .map { recordToDto(it) }
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getRobot(@PathVariable("id") id: Int): Mono<RobotRecord> {
        return repository.findById(id)
            .switchIfEmpty { error(RobotNotFoundException()) }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAllRobots(): Flux<RobotDto> {
        return repository
            .findAll()
            .map { recordToDto(it) }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun deleteRobot(@PathVariable("id") id: Int): Mono<Void> {
        return repository.deleteById(id)
    }

    @PostMapping("/{id}/move")
    fun moveRobot(@RequestBody moveDto: MoveDto, @PathVariable id: Int): Mono<RobotDto> {
        return repository.findById(id)
            .map { it.applyMove(moveDto) }
            .flatMap { repository.save(it) }
            .map { recordToDto(it) }
    }

}

data class MoveDto(
    val latitude: Int,
    val longitude: Int
)

data class RobotDto(
    val id: Int? = null,
    val name: String,
    val latitude: Int = 0,
    val longitude: Int = 0
)

@Table("robots")
data class RobotRecord(
    @Id val id: Int?,
    val name: String,
    val latitude: Int,
    val longitude: Int
)

private fun RobotRecord.applyMove(moveDto: MoveDto) = this.copy(
    latitude = this.latitude + moveDto.latitude,
    longitude = this.longitude + moveDto.longitude
)

fun dtoToRecord(dto: RobotDto) = RobotRecord(
    id = dto.id,
    name = dto.name,
    latitude = dto.latitude,
    longitude = dto.longitude
)

fun recordToDto(dto: RobotRecord) = RobotDto(
    id = dto.id,
    name = dto.name,
    latitude = dto.latitude,
    longitude = dto.longitude
)

@Repository
interface RobotsRepository : ReactiveCrudRepository<RobotRecord, Int>

@ResponseStatus(HttpStatus.NOT_FOUND)
class RobotNotFoundException : Exception()