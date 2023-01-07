package com.gt.robotrack.api

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
class BaseApiTests {

    companion object {
        @Container
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:15-alpine")

        @DynamicPropertySource
        @JvmStatic
        fun dynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url", BaseApiTests::r2dbcUrl)
            registry.add("spring.r2dbc.username", postgres::getUsername)
            registry.add("spring.r2dbc.password", postgres::getPassword)
            registry.add("spring.flyway.url", postgres::getJdbcUrl)
            registry.add("spring.flyway.user", postgres::getUsername)
            registry.add("spring.flyway.password", postgres::getPassword)
        }

        private fun r2dbcUrl() =
            "r2dbc:postgresql://${postgres.host}:${postgres.firstMappedPort}/${postgres.databaseName}"
    }

}