package com.example.demo.infrastructure.driving.controller

import com.example.demo.domain.model.Book
import com.example.demo.domain.usecase.BookService
import com.example.demo.infrastructure.driving.controller.dto.BookDTO
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookController(private val bookService: BookService) {

    @GetMapping
    fun getAllBooks(): List<BookDTO> {
        return bookService.getAllBooks().map { book ->
            BookDTO(
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
