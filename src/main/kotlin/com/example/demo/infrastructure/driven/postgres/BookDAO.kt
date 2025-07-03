package com.example.demo.infrastructure.driven.postgres

import com.example.demo.domain.model.Book
import com.example.demo.domain.port.BookRepository
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class BookDAO(private val jdbcTemplate: NamedParameterJdbcTemplate) : BookRepository {

    override fun save(book: Book) {
        val sql = "INSERT INTO book (title, author, is_reserved) VALUES (:title, :author, :isReserved)"
        val params = MapSqlParameterSource()
            .addValue("title", book.title)
            .addValue("author", book.author)
            .addValue("isReserved", false)
        jdbcTemplate.update(sql, params)
    }
    override fun findAll(): List<Book> {
        val sql = "SELECT id, title, author, is_reserved FROM book"
        return jdbcTemplate.query(sql) { rs, _ ->
            Book(
                id = rs.getLong("id"),
                title = rs.getString("title"),
                author = rs.getString("author"),
                isReserved = rs.getBoolean("is_reserved")
            )
        }
    }

    override fun reserveBook(id: Long): Boolean {
        val sql = "UPDATE book SET is_reserved = TRUE WHERE id = :id AND is_reserved = FALSE"
        val params = MapSqlParameterSource().addValue("id", id)
        return jdbcTemplate.update(sql, params) > 0
    }
}
