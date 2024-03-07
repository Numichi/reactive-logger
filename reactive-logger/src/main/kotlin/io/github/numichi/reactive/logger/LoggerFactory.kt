package io.github.numichi.reactive.logger

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.KLogger
import org.slf4j.Logger

object LoggerFactory {
    @JvmStatic
    fun getLogger(string: String): Logger = org.slf4j.LoggerFactory.getLogger(string)

    @JvmStatic
    fun getLogger(clazz: Class<*>): Logger = org.slf4j.LoggerFactory.getLogger(clazz)

    @JvmStatic
    fun getLogger(logger: KLogger): Logger = logger.toSlf4j()

    @JvmStatic
    fun getKLogger(string: String): KLogger = KotlinLogging.logger(string)

    @JvmStatic
    fun getKLogger(logger: Logger): KLogger = logger.toKLogger()

    @JvmStatic
    fun getKLogger(clazz: Class<*>): KLogger = KotlinLogging.logger(clazz.name)

    @JvmStatic
    fun getKLogger(func: () -> Unit): KLogger = KotlinLogging.logger(func)
}