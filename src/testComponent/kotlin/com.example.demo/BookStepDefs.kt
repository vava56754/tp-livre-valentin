package com.example.demo

import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.web.server.LocalServerPort

class BookStepDefs {

    private lateinit var lastResponse: Response

    @LocalServerPort
    private var port: Int = 0

    @Before
    fun setup() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    @Given("the user creates the book {string} by {string}")
    fun createBook(title: String, author: String) {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(
                """
                {
                    "title": "$title",
                    "author": "$author"
                }
                """.trimIndent()
            )
            .`when`()
            .post("/books")
            .then()
            .statusCode(201)
    }

    @When("the user gets all books")
    fun getAllBooks() {
        lastResponse = RestAssured.given()
            .`when`()
            .get("/books")
            .then()
            .statusCode(200)
            .extract()
            .response()
    }

    @Then("the list should contain the following books")
    fun shouldContainBooks(payload: List<Map<String, String?>>) {
        val expectedBooks = payload.map {
            mapOf(
                "title" to (it["title"] ?: error("Title cannot be null")),
                "author" to (it["author"] ?: error("Author cannot be null")),
                "reserved" to false
            )
        }
        val actualBooks = lastResponse.jsonPath().getList<Map<String, Any>>("").map { book ->
            book.filterKeys { it != "id" } // Ignore the 'id' field during comparison
        }
        assertThat(actualBooks).containsAll(expectedBooks)
    }

    @When("the user reserves the book with id {long}")
    fun reserveBook(id: Long) {
        lastResponse = RestAssured.given()
            .`when`()
            .post("/books/reserve/$id")
            .then()
            .extract()
            .response()
    }

    @Then("the reservation status of the book with id {long} should be {string}")
    fun verifyReservationStatus(id: Long, expectedStatus: String) {
        val books = RestAssured.given()
            .`when`()
            .get("/books")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList<Map<String, Any>>("")

        val book = books.find { it["id"].toString().toLong() == id }
        assertThat(book).withFailMessage("Book with id $id not found").isNotNull
        assertThat(book!!["reserved"]).isEqualTo(expectedStatus.toBoolean())
    }
}
