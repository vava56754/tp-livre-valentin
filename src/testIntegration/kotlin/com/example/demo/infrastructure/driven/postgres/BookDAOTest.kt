package com.example.demo.infrastructure.driven.postgres

import com.example.demo.domain.model.Book
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import io.kotest.matchers.shouldBe

@Testcontainers
@SpringBootTest
class BookDAOTest {

    @Autowired
    private lateinit var bookDAO: BookDAO

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    companion object {
        @Container
        val postgresContainer = PostgreSQLContainer<Nothing>("postgres:15.3").apply {
            withDatabaseName("testdb")
            withUsername("testuser")
            withPassword("testpass")
        }
    }

    @BeforeEach
    fun setup() {
        jdbcTemplate.execute(
            """
            CREATE TABLE IF NOT EXISTS book (
                id SERIAL PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                author VARCHAR(255) NOT NULL
            )
            """
        )
    }

    @AfterEach
    fun cleanup() {
        jdbcTemplate.execute("TRUNCATE TABLE book")
    }

    @Test
    fun `should save and retrieve books`() {
        val book = Book(title = "Test Book", author = "Test Author")
        bookDAO.save(book)

        val books = bookDAO.findAll()
        books.size shouldBe 1
        books[0].title shouldBe "Test Book"
        books[0].author shouldBe "Test Author"
    }
}