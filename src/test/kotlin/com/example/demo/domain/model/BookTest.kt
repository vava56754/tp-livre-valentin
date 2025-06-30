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

    "should verify equality of two books with the same properties" {
        val book1 = Book(title = "Clean Code", author = "Robert C. Martin")
        val book2 = Book(title = "Clean Code", author = "Robert C. Martin")

        book1 shouldBe book2
    }
})