package com.example.demo.infrastructure.driving.controller

import com.example.demo.domain.model.Book
import com.example.demo.domain.usecase.BookService
import com.example.demo.infrastructure.driving.controller.dto.BookDTO
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookController(private val bookService: BookService) {

    @GetMapping
    fun getAllBooks(): List<BookDTO> {
        return bookService.getAllBooks().map { book ->
            BookDTO(
                id = book.id,
                title = book.title,
                author = book.author,
                isReserved = book.isReserved
            )
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createBook(@RequestBody bookDTO: BookDTO) {
        val book = Book(title = bookDTO.title, author = bookDTO.author)
        bookService.addBook(book)
    }

    @PostMapping("/reserve/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun reserveBook(@PathVariable id: Long): Boolean {
        return bookService.reserveBook(id)
    }
}
