package io.github.numichi.reactive.logger

import mu.KotlinLogging
import org.slf4j.Logger

object LoggerFactory {
    fun getLogger(string: String) = org.slf4j.LoggerFactory.getLogger(string)
    fun getLogger(clazz: Class<*>) = org.slf4j.LoggerFactory.getLogger(clazz)

    fun getKLogger(string: String) = KotlinLogging.logger(string)
    fun getKLogger(logger: Logger) = KotlinLogging.logger(logger)
    fun getKLogger(clazz: Class<*>) = KotlinLogging.logger(org.slf4j.LoggerFactory.getLogger(clazz))
    fun getKLogger(func: () -> Unit) = KotlinLogging.logger(func)
}