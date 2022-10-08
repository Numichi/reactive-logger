package io.github.numichi.reactive.logger.spring.beans

import io.github.numichi.reactive.logger.LoggerFactory
import io.github.numichi.reactive.logger.coroutine.CoroutineKLogger
import io.github.numichi.reactive.logger.coroutine.CoroutineLogger
import io.github.numichi.reactive.logger.reactor.ReactiveKLogger
import io.github.numichi.reactive.logger.reactor.ReactiveLogger
import io.github.numichi.reactive.logger.spring.DefaultValuesAutoConfiguration
import io.github.numichi.reactive.logger.spring.beans.helper.CacheKKey
import io.github.numichi.reactive.logger.spring.beans.helper.CacheKey
import io.github.numichi.reactive.logger.spring.properties.InstanceProperties
import io.github.numichi.reactive.logger.spring.properties.ReactiveLoggerProperties
import mu.KLogger
import org.slf4j.Logger
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(value = [ReactiveLoggerProperties::class])
@AutoConfigureAfter(DefaultValuesAutoConfiguration::class)
open class LoggerRegistryImpl(private val properties: ReactiveLoggerProperties) : LoggerRegistry {
    private val instances: Map<String, InstanceProperties> = properties.instances
    private val storeReactiveLogger = mutableMapOf<CacheKey, ReactiveLogger>()
    private val storeReactiveKLogger = mutableMapOf<CacheKKey, ReactiveKLogger>()
    private val storeCoroutineLogger = mutableMapOf<CacheKey, CoroutineLogger>()
    private val storeCoroutineKLogger = mutableMapOf<CacheKKey, CoroutineKLogger>()

    override fun reset() {
        DefaultValuesAutoConfiguration.reset(properties, true)
    }

    private fun createLogger(logger: Any?, default: Class<*>): Logger {
        if (logger == null) {
            return LoggerFactory.getLogger(default)
        }

        if (logger is Class<*>) {
            return LoggerFactory.getLogger(logger)
        }

        return LoggerFactory.getLogger(logger.toString())
    }

    private fun createKLogger(logger: Any?, default: Class<*>): KLogger {
        if (logger == null) {
            return LoggerFactory.getKLogger(default)
        }

        if (logger is Class<*>) {
            return LoggerFactory.getKLogger(logger)
        }

        return LoggerFactory.getKLogger(logger.toString())
    }

    override fun getReactiveLogger(instance: String): ReactiveLogger {
        return getReactiveLogger(
            instance,
            createLogger(instances[instance]?.logger, ReactiveLogger::class.java)
        )
    }

    override fun getReactiveLogger(instance: String, logger: String): ReactiveLogger {
        return getReactiveLogger(
            instance,
            createKLogger(logger, ReactiveLogger::class.java)
        )
    }

    override fun getReactiveLogger(instance: String, logger: Class<*>): ReactiveLogger {
        return getReactiveLogger(
            instance,
            createKLogger(logger, ReactiveLogger::class.java)
        )
    }

    override fun getReactiveLogger(instance: String, logger: Logger): ReactiveLogger {
        val cacheKey = CacheKey(instance, logger.name, instances[instance]?.contextKey, instances[instance]?.scheduler?.toScheduler())
        var cached = storeReactiveLogger[cacheKey]

        if (cached == null) {
            cached = ReactiveLogger.getLogger(logger, cacheKey.contextKey, cacheKey.scheduler)
            storeReactiveLogger[cacheKey] = cached
        }

        return cached
    }

    override fun getReactiveKLogger(instance: String): ReactiveKLogger {
        return getReactiveKLogger(
            instance,
            createKLogger(instances[instance]?.logger, ReactiveKLogger::class.java)
        )
    }

    override fun getReactiveKLogger(instance: String, logger: String): ReactiveKLogger {
        return getReactiveKLogger(
            instance,
            createKLogger(logger, ReactiveKLogger::class.java)
        )
    }

    override fun getReactiveKLogger(instance: String, logger: Class<*>): ReactiveKLogger {
        return getReactiveKLogger(
            instance,
            createKLogger(logger, ReactiveKLogger::class.java)
        )
    }

    override fun getReactiveKLogger(instance: String, logger: KLogger): ReactiveKLogger {
        val cacheKey = CacheKKey(instance, logger.name, instances[instance]?.contextKey, instances[instance]?.scheduler?.toScheduler())
        var cached = storeReactiveKLogger[cacheKey]

        if (cached == null) {
            cached = ReactiveKLogger.getLogger(logger, cacheKey.contextKey, cacheKey.scheduler)
            storeReactiveKLogger[cacheKey] = cached
        }

        return cached
    }

    override fun getCoroutineLogger(instance: String): CoroutineLogger {
        return getCoroutineLogger(
            instance,
            createLogger(instances[instance]?.logger, CoroutineLogger::class.java)
        )
    }

    override fun getCoroutineLogger(instance: String, logger: String): CoroutineLogger {
        return getCoroutineLogger(
            instance,
            createLogger(logger, CoroutineLogger::class.java)
        )
    }

    override fun getCoroutineLogger(instance: String, logger: Class<*>): CoroutineLogger {
        return getCoroutineLogger(
            instance,
            createLogger(logger, CoroutineLogger::class.java)
        )
    }

    override fun getCoroutineLogger(instance: String, logger: Logger): CoroutineLogger {
        val cacheKey = CacheKey(instance, logger.name, instances[instance]?.contextKey, instances[instance]?.scheduler?.toScheduler())
        var cached = storeCoroutineLogger[cacheKey]

        if (cached == null) {
            cached = CoroutineLogger.getLogger(logger, cacheKey.contextKey, cacheKey.scheduler)
            storeCoroutineLogger[cacheKey] = cached
        }

        return cached
    }

    override fun getCoroutineKLogger(instance: String): CoroutineKLogger {
        return getCoroutineKLogger(
            instance,
            createKLogger(instances[instance]?.logger, CoroutineKLogger::class.java),
        )
    }

    override fun getCoroutineKLogger(instance: String, logger: String): CoroutineKLogger {
        return getCoroutineKLogger(
            instance,
            createKLogger(logger, CoroutineKLogger::class.java)
        )
    }

    override fun getCoroutineKLogger(instance: String, logger: Class<*>): CoroutineKLogger {
        return getCoroutineKLogger(
            instance,
            createKLogger(logger, CoroutineKLogger::class.java)
        )
    }

    override fun getCoroutineKLogger(instance: String, logger: KLogger): CoroutineKLogger {
        val cacheKey = CacheKKey(instance, logger.name, instances[instance]?.contextKey, instances[instance]?.scheduler?.toScheduler())
        var cached = storeCoroutineKLogger[cacheKey]

        if (cached == null) {
            cached = CoroutineKLogger.getLogger(logger, cacheKey.contextKey, cacheKey.scheduler)
            storeCoroutineKLogger[cacheKey] = cached
        }

        return cached
    }
}