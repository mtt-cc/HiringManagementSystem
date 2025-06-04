package it.polito.waii_24.g20.com_manager.integration

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = [IntegrationTest.Initializer::class])
abstract class IntegrationTest {
    companion object {
        private val db = PostgreSQLContainer("postgres:latest")
    }

    internal class Initializer: ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            db.start()
            TestPropertyValues.of(
                "spring.datasource.url=${db.jdbcUrl}",
                "spring.datasource.username=${db.username}",
                "spring.datasource.password=${db.password}"
            ).applyTo(applicationContext.environment)
        }
    }

    @Test
    fun containerIsUpAndRunning() {
        assert(db.isRunning)
    }
}