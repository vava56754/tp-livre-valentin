package com.example.demo.domain.model

data class Book(
    val id: Long? = null,
    val author: String,
    val isReserved: Boolean = false,
    val title: String,
) {
    init {
        require(author.isNotBlank()) { "Book author must not be empty or blank" }
        require(title.isNotBlank()) { "Book title must not be empty or blank" }
    }
}
