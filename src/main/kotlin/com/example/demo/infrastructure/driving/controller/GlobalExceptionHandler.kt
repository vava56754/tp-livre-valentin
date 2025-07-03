package com.example.demo.infrastructure.driving.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        return ResponseEntity(mapOf("error" to ex.message!!), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(): ResponseEntity<Map<String, String>> {
        return ResponseEntity(mapOf("error" to "An unexpected error occurred"), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(ex: IllegalStateException): ResponseEntity<Map<String, String>> {
        return ResponseEntity(mapOf("error" to ex.message!!), HttpStatus.BAD_REQUEST)
    }
}
