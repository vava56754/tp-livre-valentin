package com.example.demo.domain.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class BookTest : StringSpec({
    "should create a book with correct title and author" {
        val book = Book(title = "Clean Code", author = "Robert C. Martin")

        book.title shouldBe "Clean Code"
        book.author shouldBe "Robert C. Martin"
    }

    "should default isReserved to false" {
        val book = Book(title = "Clean Code", author = "Robert C. Martin")
        book.isReserved shouldBe false
    }

    "should allow creating a reserved book" {
        val book = Book(title = "Clean Code", author = "Robert C. Martin", isReserved = true)
        book.isReserved shouldBe true
    }

    "should verify equality of two books with the same properties when is Reserved is false" {
        val book1 = Book(title = "Clean Code", author = "Robert C. Martin", isReserved = false)
        val book2 = Book(title = "Clean Code", author = "Robert C. Martin", isReserved = false)

        book1 shouldBe book2
    }

    "should verify equality of two books with the same properties when is Reserved is true" {
        val book1 = Book(title = "Clean Code", author = "Robert C. Martin", isReserved = true)
        val book2 = Book(title = "Clean Code", author = "Robert C. Martin", isReserved = true)

        book1 shouldBe book2
    }

    "should throw an exception if title is blank" {
        val exception = shouldThrow<IllegalArgumentException> {
            Book(title = "   ", author = "Robert C. Martin")
        }
        exception.message shouldBe "Book title must not be empty or blank"
    }

    "should throw an exception if author is blank" {
        val exception = shouldThrow<IllegalArgumentException> {
            Book(title = "Clean Code", author = "   ")
        }
        exception.message shouldBe "Book author must not be empty or blank"
    }

    "should throw an exception if title is empty" {
        val exception = shouldThrow<IllegalArgumentException> {
            Book(title = "", author = "Robert C. Martin")
        }
        exception.message shouldBe "Book title must not be empty or blank"
    }

    "should throw an exception if author is empty" {
        val exception = shouldThrow<IllegalArgumentException> {
            Book(title = "Clean Code", author = "")
        }
        exception.message shouldBe "Book author must not be empty or blank"
    }
})
