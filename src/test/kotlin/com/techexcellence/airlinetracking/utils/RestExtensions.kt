package com.techexcellence.airlinetracking.utils

import com.techexcellence.airlinetracking.airplanes.AirplaneDto
import io.restassured.module.webtestclient.response.ValidatableWebTestClientResponse
import io.restassured.module.webtestclient.specification.WebTestClientRequestSpecification
import org.springframework.http.HttpStatus


fun ValidatableWebTestClientResponse.extractBodyAsAirplane(): AirplaneDto = this.extract()
    .body()
    .`as`(AirplaneDto::class.java)

fun WebTestClientRequestSpecification.airplaineIsTracked(
    name: String,
    latitude: Int = 0,
    longitude: Int = 0
): AirplaneDto = this
    .body(AirplaneDto(name = name, latitude = latitude, longitude = longitude))
    .post("/airplanes")
    .then()
    .status(HttpStatus.CREATED)
    .extractBodyAsAirplane()