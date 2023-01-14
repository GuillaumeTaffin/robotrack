package com.gt.robotrack.api.robots

import com.gt.robotrack.api.BaseApiTests
import com.gt.robotrack.utils.robotIsCreated
import io.restassured.module.webtestclient.RestAssuredWebTestClient.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient

class RemoveRobotTests(@Autowired webTestClient: WebTestClient) : BaseApiTests(webTestClient) {

    @Test
    fun `Should remove existing robot`() {
        val ruddy = given().robotIsCreated("Ruddy")

        delete("robots/${ruddy.id}")
            .then()
            .status(HttpStatus.OK)

        get("robots/${ruddy.id}")
            .then()
            .status(HttpStatus.NOT_FOUND)
    }
    
}