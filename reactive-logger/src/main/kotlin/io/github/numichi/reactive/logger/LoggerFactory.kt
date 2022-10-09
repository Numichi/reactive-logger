package io.github.numichi.reactive.logger

import mu.KotlinLogging
import org.slf4j.Logger

object LoggerFactory {
    @JvmStatic
    fun getLogger(string: String): Logger = org.slf4j.LoggerFactory.getLogger(string)
    @JvmStatic
    fun getLogger(clazz: Class<*>): Logger = org.slf4j.LoggerFactory.getLogger(clazz)

    @JvmStatic
    fun getKLogger(string: String) = KotlinLogging.logger(string)
    @JvmStatic
    fun getKLogger(logger: Logger) = KotlinLogging.logger(logger)
    @JvmStatic
    fun getKLogger(clazz: Class<*>) = KotlinLogging.logger(org.slf4j.LoggerFactory.getLogger(clazz))
    @JvmStatic
    fun getKLogger(func: () -> Unit) = KotlinLogging.logger(func)
}