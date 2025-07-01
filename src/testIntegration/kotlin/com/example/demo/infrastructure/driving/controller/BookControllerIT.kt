package com.example.demo.infrastructure.driving.controller

import com.example.demo.domain.model.Book
import com.example.demo.domain.usecase.BookService
import com.example.demo.infrastructure.driving.controller.dto.BookDTO
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(controllers = [BookController::class])
class BookControllerIT {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var bookService: BookService

    @Test
    fun `GET all books should return a list of books`() {
        // Arrange
        val books = listOf(
            Book("Title1", "Author1"),
            Book("Title2", "Author2")
        )
        every { bookService.getAllBooks() } returns books

        // Act & Assert
        mockMvc.get("/books")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.length()") { value(2) }
                jsonPath("$[0].title") { value("Title1") }
                jsonPath("$[0].author") { value("Author1") }
            }

        verify { bookService.getAllBooks() }
    }

    @Test
    fun `POST create book should return 201 when input is valid`() {
        // Arrange
        val bookDTO = BookDTO("Title1", "Author1")
        every { bookService.addBook(any()) } returns Unit

        // Act & Assert
        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"title": "Title1", "author": "Author1"}"""
        }.andExpect {
            status { isCreated() }
        }

        verify { bookService.addBook(Book("Title1", "Author1")) }
    }

    @Test
    fun `POST create book should return 400 when input is invalid`() {
        // Act & Assert
        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"title": "", "author": ""}"""
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `GET all books should return 500 when service throws exception`() {
        // Arrange
        every { bookService.getAllBooks() } throws RuntimeException("Unexpected error")

        // Act & Assert
        mockMvc.get("/books")
            .andExpect {
                status { isInternalServerError() }
            }

        verify { bookService.getAllBooks() }
    }
}