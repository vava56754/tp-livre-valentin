package com.example.demo.domain.usecase

import com.example.demo.domain.model.Book
import com.example.demo.domain.port.BookRepository

class BookService(private val bookRepository: BookRepository) {

    private val books = mutableListOf<Book>()

    fun addBook(book: Book) {
        require(book.title.isNotBlank()) { "Book title must not be empty" }
        require(book.author.isNotBlank()) { "Book author must not be empty" }
        bookRepository.save(book)
    }

    fun getAllBooks(): List<Book> {
        return bookRepository.findAll().sortedBy { it.title }
    }
}