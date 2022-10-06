package io.github.numichi.reactive.logger.example.kotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ReactiveLoggerExampleApplication

fun main(args: Array<String>) {
    runApplication<ReactiveLoggerExampleApplication>(*args)
}
