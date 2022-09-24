package io.github.numichi.reactive.logger.spring.beans

import io.github.numichi.reactive.logger.LoggerFactory
import io.github.numichi.reactive.logger.coroutine.CoroutineKLogger
import io.github.numichi.reactive.logger.coroutine.CoroutineLogger
import io.github.numichi.reactive.logger.reactor.ReactiveKLogger
import io.github.numichi.reactive.logger.reactor.ReactiveLogger
import io.github.numichi.reactive.logger.spring.properties.Instances
import mu.KLogger
import org.slf4j.Logger

open class LoggerRegistryImpl(private val instances: Map<String, Instances>) : LoggerRegistry {
    private fun createLogger(logger: String?): Logger? {
        if (logger == null) return null

        return try {
            LoggerFactory.getLogger(Class.forName(logger))
        } catch (e: Exception) {
            LoggerFactory.getLogger(logger)
        }
    }

    private fun createKLogger(logger: String?): KLogger? {
        if (logger == null) return null

        return try {
            LoggerFactory.getKLogger(Class.forName(logger))
        } catch (e: Exception) {
            LoggerFactory.getKLogger(logger)
        }
    }

    override fun makeReactiveLogger(instance: String): ReactiveLogger {
        val logger = createLogger(instances[instance]?.logger)
            ?: LoggerFactory.getLogger(ReactiveLogger::class.java)

        return ReactiveLogger.getLogger(
            logger,
            instances[instance]?.contextKey,
            instances[instance]?.scheduler?.toScheduler()
        )
    }

    override fun makeReactiveKLogger(instance: String): ReactiveKLogger {
        val logger = createKLogger(instances[instance]?.logger)
            ?: LoggerFactory.getKLogger(ReactiveKLogger::class.java)

        return ReactiveKLogger.getLogger(
            logger,
            instances[instance]?.contextKey,
            instances[instance]?.scheduler?.toScheduler()
        )
    }

    override fun makeCoroutineLogger(instance: String): CoroutineLogger {
        val logger = createLogger(instances[instance]?.logger)
            ?: LoggerFactory.getLogger(CoroutineLogger::class.java)

        return CoroutineLogger.getLogger(
            logger,
            instances[instance]?.contextKey,
            instances[instance]?.scheduler?.toScheduler()
        )
    }

    override fun makeCoroutineKLogger(instance: String): CoroutineKLogger {
        val logger = createKLogger(instances[instance]?.logger)
            ?: LoggerFactory.getKLogger(CoroutineKLogger::class.java)

        return CoroutineKLogger.getLogger(
            logger,
            instances[instance]?.contextKey,
            instances[instance]?.scheduler?.toScheduler()
        )
    }
}