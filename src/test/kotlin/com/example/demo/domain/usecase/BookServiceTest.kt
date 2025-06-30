package com.example.demo.domain.usecase

import com.example.demo.domain.model.Book
import com.example.demo.domain.port.BookRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class BookServiceTest : StringSpec({

    val bookRepository = mockk<BookRepository>(relaxed = true)
    val bookService = BookService(bookRepository)

    "should add a book with valid title and author" {
        val book = Book(title = "Clean Code", author = "Robert C. Martin")

        bookService.addBook(book)

        verify { bookRepository.save(book) }
    }

    "should throw an exception if title is empty" {
        val book = Book(title = "", author = "Author")

        val exception = shouldThrow<IllegalArgumentException> {
            bookService.addBook(book)
        }

        exception.message shouldBe "Book title must not be empty"
    }

    "should throw an exception if author is empty" {
        val book = Book(title = "Title", author = "")

        val exception = shouldThrow<IllegalArgumentException> {
            bookService.addBook(book)
        }

        exception.message shouldBe "Book author must not be empty"
    }

    "should return all books sorted by title" {
        val books = listOf(
            Book(title = "The Pragmatic Programmer", author = "Andy Hunt"),
            Book(title = "Clean Code", author = "Robert C. Martin"),
            Book(title = "Refactoring", author = "Martin Fowler")
        )
        every { bookRepository.findAll() } returns books

        val result = bookService.getAllBooks()

        result shouldBe books.sortedBy { it.title }
    }

    "the list of books returned should contain all elements of the stored list" {

        checkAll(Arb.list(Arb.stringPattern("[a-zA-Z0-9]+"), 1..10)) { titles ->
            val books = titles.map { title -> Book(title = title, author = "Author") }

            // Mock the repository to return the books added
            every { bookRepository.findAll() } returns books

            books.forEach { bookService.addBook(it) }

            val result = bookService.getAllBooks()

            result shouldContainExactlyInAnyOrder books
        }
    }
})