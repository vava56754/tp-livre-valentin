package com.example.demo.infrastructure.driven.postgres

import com.example.demo.domain.model.Book
import com.example.demo.domain.port.BookRepository
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository

@Repository
class BookDAO(private val jdbcTemplate: NamedParameterJdbcTemplate) : BookRepository {

    override fun save(book: Book) {
        val sql = "INSERT INTO book (title, author) VALUES (:title, :author)"
        val params = MapSqlParameterSource()
            .addValue("title", book.title)
            .addValue("author", book.author)
        jdbcTemplate.update(sql, params)
    }

    override fun findAll(): List<Book> {
        val sql = "SELECT title, author FROM book"
        return jdbcTemplate.query(sql) { rs, _ ->
            Book(
                title = rs.getString("title"),
                author = rs.getString("author")
            )
        }
    }
}