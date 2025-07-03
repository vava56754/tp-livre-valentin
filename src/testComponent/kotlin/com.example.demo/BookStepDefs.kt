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
                "id" to (it["id"] ?: error("ID cannot be null")),
                "title" to (it["title"] ?: error("Title cannot be null")),
                "author" to (it["author"] ?: error("Author cannot be null"))
            )
        }
        val actualBooks = lastResponse.jsonPath().getList<Map<String, String>>("")
        assertThat(actualBooks).containsAll(expectedBooks)
    }

    @When("the user reserves the book {string}")
    fun reserveBook(id: Long) {
        lastResponse = RestAssured.given()
            .`when`()
            .post("/books/$id/reserve")
            .then()
            .statusCode(200)
            .extract()
            .response()
    }

    @Then("the book {string} is reserved")
    fun bookIsReserved(id: Long) {
        val all = RestAssured.given()
            .`when`()
            .get("/books")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList<Map<String, Any>>("")

        val book = all.find { it["id"] == id }
            ?: error("Book $id not found in response")

        assertThat(book["isReserved"] as Boolean).isTrue
    }
}