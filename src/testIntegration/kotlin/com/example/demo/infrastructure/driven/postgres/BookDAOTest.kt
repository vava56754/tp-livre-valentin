package com.example.demo.infrastructure.driven.postgres

import com.example.demo.domain.model.Book
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.ResultSet

@SpringBootTest
@ActiveProfiles("testIntegration")
class BookDAOTest(
    private val bookDAO: BookDAO
) : StringSpec() {
    init {
        extension(SpringExtension)

        beforeTest {
            performQuery(
                // language=sql
                "DELETE FROM book"
            )
        }

        "should retrieve all books from the database" {
            // GIVEN
            performQuery(
                """
                INSERT INTO book (id, title, author, is_reserved)
                VALUES
                    (1, '1984', 'George Orwell', false),
                    (2, 'To Kill a Mockingbird', 'Harper Lee', false),
                    (3, 'The Great Gatsby', 'F. Scott Fitzgerald', true);
                """.trimIndent()
            )

            // WHEN
            val books = bookDAO.findAll()

            // THEN
            books.shouldContainExactlyInAnyOrder(
                Book(id = 1L, title = "1984", author = "George Orwell", isReserved = false),
                Book(id = 2L, title = "To Kill a Mockingbird", author = "Harper Lee", isReserved = false),
                Book(id = 3L, title = "The Great Gatsby", author = "F. Scott Fitzgerald", isReserved = true)
            )
        }

        "should add a book to the database" {
            // GIVEN
            val book = Book(title = "Pride and Prejudice", author = "Jane Austen")

            // WHEN
            bookDAO.save(book)

            // THEN
            val result = performQuery(
                "SELECT * FROM book"
            )

            result shouldHaveSize 1
            assertSoftly(result.first()) {
                this["id"].shouldNotBeNull().shouldBeInstanceOf<Int>()
                this["title"].shouldBe("Pride and Prejudice")
                this["author"].shouldBe("Jane Austen")
                this["is_reserved"].shouldBe(false)
            }
        }

        "should reserve a book in the database" {
            // GIVEN
            performQuery(
                """
                INSERT INTO book (id, title, author, is_reserved)
                VALUES (1, '1984', 'George Orwell', false);
                """.trimIndent()
            )

            // WHEN
            val result = bookDAO.reserveBook(1L)

            // THEN
            result shouldBe true
            val updatedBook = performQuery(
                "SELECT * FROM book WHERE id = 1"
            ).first()

            assertSoftly(updatedBook) {
                this["id"].shouldBe(1)
                this["title"].shouldBe("1984")
                this["author"].shouldBe("George Orwell")
                this["is_reserved"].shouldBe(true)
            }
        }

        afterSpec {
            container.stop()
        }
    }

    companion object {
        private val container = PostgreSQLContainer<Nothing>("postgres:15.3")

        init {
            container.start()
            System.setProperty("spring.datasource.url", container.jdbcUrl)
            System.setProperty("spring.datasource.username", container.username)
            System.setProperty("spring.datasource.password", container.password)
        }

        private fun ResultSet.toList(): List<Map<String, Any>> {
            val metaData = this.metaData
            val columnCount = metaData.columnCount
            val rows = mutableListOf<Map<String, Any>>()
            while (this.next()) {
                val row = mutableMapOf<String, Any>()
                for (i in 1..columnCount) {
                    row[metaData.getColumnName(i)] = this.getObject(i)
                }
                rows.add(row)
            }
            return rows
        }

        fun performQuery(sql: String): List<Map<String, Any>> {
            val hikariConfig = HikariConfig().apply {
                jdbcUrl = container.jdbcUrl
                username = container.username
                password = container.password
                driverClassName = container.driverClassName
            }
            HikariDataSource(hikariConfig).use { dataSource ->
                dataSource.connection.use { connection ->
                    connection.createStatement().use { statement ->
                        statement.execute(sql)
                        val resultSet = statement.resultSet
                        return resultSet?.toList() ?: emptyList()
                    }
                }
            }
        }
    }
}
