package com.example.demo.domain.usecase

import com.example.demo.domain.model.Book
import com.example.demo.domain.port.BookRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
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
        val exception = shouldThrow<IllegalArgumentException> {
            bookService.addBook(Book(title = "   ", author = "Author"))
        }

        exception.message shouldBe "Book title must not be empty or blank"
    }

    "should throw an exception if author is empty" {
        val exception = shouldThrow<IllegalArgumentException> {
            bookService.addBook(Book(title = "Title", author = "   "))
        }

        exception.message shouldBe "Book author must not be empty or blank"
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

    "reserveBook returns true and calls repository when book exists and not reserved" {
        val book = Book(id = 42L, title = "Clean Code", author = "Robert C. Martin", isReserved = false)
        every { bookRepository.findAll() } returns listOf(book)
        every { bookRepository.reserveBook(book.id!!) } returns true

        val result = bookService.reserveBook(book.id!!)

        result shouldBe true
        verify { bookRepository.reserveBook(book.id!!) }
    }

    "reserveBook should throw an exception when book not found" {
        every { bookRepository.findAll() } returns emptyList()

        shouldThrow<IllegalStateException> {
            bookService.reserveBook(999L)
        }.message shouldBe "Book is already reserved or does not exist"
    }

    "reserveBook should throw an exception when already reserved" {
        val book = Book(id = 43L, title = "Clean Code", author = "Robert C. Martin", isReserved = true)
        every { bookRepository.findAll() } returns listOf(book)

        shouldThrow<IllegalStateException> {
            bookService.reserveBook(book.id!!)
        }.message shouldBe "Book is already reserved or does not exist"
    }

})
