package com.techexcellence.airlinetracking.api

import io.restassured.config.JsonConfig
import io.restassured.config.LogConfig
import io.restassured.http.ContentType
import io.restassured.module.webtestclient.RestAssuredWebTestClient
import io.restassured.module.webtestclient.config.RestAssuredWebTestClientConfig
import io.restassured.module.webtestclient.specification.WebTestClientRequestSpecBuilder
import io.restassured.path.json.config.JsonPathConfig
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.lifecycle.Startables

@Testcontainers
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
abstract class BaseApiTests(
    val webTestClient: WebTestClient
) {

    companion object {
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:15-alpine")

        @DynamicPropertySource
        @JvmStatic
        fun dynamicProperties(registry: DynamicPropertyRegistry) {
            Startables.deepStart(postgres).join()

            registry.add("spring.r2dbc.url", Companion::r2dbcUrl)
            registry.add("spring.r2dbc.username", postgres::getUsername)
            registry.add("spring.r2dbc.password", postgres::getPassword)
            registry.add("spring.flyway.url", postgres::getJdbcUrl)
            registry.add("spring.flyway.user", postgres::getUsername)
            registry.add("spring.flyway.password", postgres::getPassword)
        }

        @JvmStatic
        private fun r2dbcUrl() =
            "r2dbc:postgresql://${postgres.host}:${postgres.firstMappedPort}/${postgres.databaseName}"

    }

    @BeforeEach
    fun setup() {
        RestAssuredWebTestClient.webTestClient(webTestClient)
        RestAssuredWebTestClient.config = RestAssuredWebTestClientConfig.newConfig()
            .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails())
            .jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE))

        RestAssuredWebTestClient.requestSpecification = WebTestClientRequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .build()
    }

}