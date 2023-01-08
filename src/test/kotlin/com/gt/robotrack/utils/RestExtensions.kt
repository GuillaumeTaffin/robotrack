package com.gt.robotrack.utils

import com.gt.robotrack.robots.RobotDto
import io.restassured.module.webtestclient.response.ValidatableWebTestClientResponse
import io.restassured.module.webtestclient.specification.WebTestClientRequestSpecification
import org.springframework.http.HttpStatus


fun ValidatableWebTestClientResponse.extractBodyAsRobot(): RobotDto = this.extract()
    .body()
    .`as`(RobotDto::class.java)

fun WebTestClientRequestSpecification.robotIsCreated(name: String): RobotDto = this
    .body(RobotDto(name = name))
    .post("/robots")
    .then()
    .status(HttpStatus.CREATED)
    .extractBodyAsRobot()