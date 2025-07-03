package com.example.demo.domain.model

data class Book(
    val title: String,
    val author: String,
    val isReserved: Boolean = false,
) {
    init {
        require(title.isNotBlank()) { "Book title must not be empty or blank" }
        require(author.isNotBlank()) { "Book author must not be empty or blank" }
    }
}
