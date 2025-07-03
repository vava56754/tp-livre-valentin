package com.example.demo.infrastructure.driving.controller

import com.example.demo.domain.model.Book
import com.example.demo.domain.usecase.BookService
import com.example.demo.infrastructure.driving.controller.dto.BookDTO
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
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
            Book(id = 1L, title = "Title1", author = "Author1", isReserved = false),
            Book(id = 2L, title = "Title2", author = "Author2", isReserved = true)
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
                jsonPath("$[0].reserved") { value(false) }
                jsonPath("$[1].reserved") { value(true) }
            }

        verify { bookService.getAllBooks() }
    }

    @Test
    fun `POST create book should return 201 when input is valid`() {
        // Arrange
        val bookDTO = BookDTO(title = "Title1", author = "Author1")
        every { bookService.addBook(any()) } returns Unit

        // Act & Assert
        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"title": "Title1", "author": "Author1"}"""
        }.andExpect {
            status { isCreated() }
        }

        verify {
            bookService.addBook(
                withArg { book ->
                    book.title shouldBe "Title1"
                    book.author shouldBe "Author1"
                    book.isReserved shouldBe false
                }
            )
        }
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

    @Test
    fun `POST reserve book should return 200 when successful`() {
        // Arrange
        every { bookService.reserveBook(1L) } returns true

        // Act & Assert
        mockMvc.post("/books/reserve/1")
            .andExpect {
                status { isOk() }
            }

        verify { bookService.reserveBook(1L) }
    }

    @Test
    fun `POST reserve book should return 400 when book is already reserved`() {
        // Arrange
        every { bookService.reserveBook(1L) } throws IllegalStateException("Book is already reserved")

        // Act & Assert
        mockMvc.post("/books/reserve/1")
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.error") { value("Book is already reserved") }
            }

        verify { bookService.reserveBook(1L) }
    }
}
