package com.gt.robotrack.utils

import com.gt.robotrack.robots.RobotDto
import io.restassured.module.webtestclient.response.ValidatableWebTestClientResponse
import io.restassured.module.webtestclient.specification.WebTestClientRequestSpecification
import org.springframework.http.HttpStatus


fun ValidatableWebTestClientResponse.extractBodyAsRobot(): RobotDto = this.extract()
    .body()
    .`as`(RobotDto::class.java)

fun WebTestClientRequestSpecification.robotIsCreated(
    name: String,
    latitude: Int = 0,
    longitude: Int = 0
): RobotDto = this
    .body(RobotDto(name = name, latitude = latitude, longitude = longitude))
    .post("/robots")
    .then()
    .status(HttpStatus.CREATED)
    .extractBodyAsRobot()