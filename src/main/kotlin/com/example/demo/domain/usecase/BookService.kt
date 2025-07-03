package com.example.demo.domain.usecase

import com.example.demo.domain.model.Book
import com.example.demo.domain.port.BookRepository

class BookService(private val bookRepository: BookRepository) {

    fun addBook(book: Book) {
        bookRepository.save(book)
    }

    fun getAllBooks(): List<Book> {
        return bookRepository.findAll().sortedBy { it.title }
    }

    fun reserveBook(title: String): Boolean {
        val book = bookRepository.findAll().find { it.title == title }
        return if (book != null && !book.isReserved) {
            bookRepository.reserveBook(title)
        } else {
            false
        }
    }
}
