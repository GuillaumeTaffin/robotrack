package com.gt.robotrack.robots

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/robots")
class Robots(
    @Autowired val repository: RobotsRepository
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createRobot(@RequestBody dto: RobotDto): RobotDto {
        return recordToDto(repository.save(dtoToRecord(dto)))
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getRobot(@PathVariable("id") id: Int): RobotDto {
        val record = repository.findById(id) ?: throw RobotNotFoundException()
        return recordToDto(record)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    suspend fun getAllRobots(): Flow<RobotDto> {
        return repository.findAll()
            .map { recordToDto(it) }
    }

}

data class RobotDto(
    val id: Int? = null,
    val name: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

@Table("robots")
data class RobotRecord(
    @Id var id: Int?,
    var name: String,
    var latitude: Double,
    var longitude: Double
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
interface RobotsRepository : CoroutineCrudRepository<RobotRecord, Int>

@ResponseStatus(HttpStatus.NOT_FOUND)
class RobotNotFoundException : Exception()