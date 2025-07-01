package com.example.demo

import com.example.demo.domain.port.BookRepository
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean

@SpringBootTest
class DemoApplicationTests {

    @MockitoBean
    private lateinit var bookRepository: BookRepository

    @Test
    fun contextLoads() {
    }

}
