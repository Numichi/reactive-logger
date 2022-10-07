package io.github.numichi.reactive.logger.spring.beans

import io.github.numichi.reactive.logger.LoggerFactory
import io.github.numichi.reactive.logger.coroutine.CoroutineKLogger
import io.github.numichi.reactive.logger.coroutine.CoroutineLogger
import io.github.numichi.reactive.logger.reactor.ReactiveKLogger
import io.github.numichi.reactive.logger.reactor.ReactiveLogger
import io.github.numichi.reactive.logger.spring.properties.ReactiveLoggerProperties as ReactiveLoggerProperties
import io.github.numichi.reactive.logger.spring.properties.InstanceProperties
import mu.KLogger
import org.slf4j.Logger
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(value = [ReactiveLoggerProperties::class])
open class LoggerRegistryImpl(private val properties: ReactiveLoggerProperties) : LoggerRegistry {
    private val instances: Map<String, InstanceProperties>
        get() = properties.instances

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

    override fun makeReactiveLogger(instance: String): ReactiveLogger {
        return makeReactiveLogger(
            instance,
            createLogger(instances[instance]?.logger, ReactiveLogger::class.java)
        )
    }

    override fun makeReactiveLogger(instance: String, logger: String): ReactiveLogger {
        return makeReactiveLogger(
            instance,
            createKLogger(logger, ReactiveLogger::class.java)
        )
    }

    override fun makeReactiveLogger(instance: String, logger: Class<*>): ReactiveLogger {
        return makeReactiveLogger(
            instance,
            createKLogger(logger, ReactiveLogger::class.java)
        )
    }

    override fun makeReactiveLogger(instance: String, logger: Logger): ReactiveLogger {
        return ReactiveLogger.getLogger(
            logger,
            instances[instance]?.contextKey,
            instances[instance]?.scheduler?.toScheduler()
        )
    }

    override fun makeReactiveKLogger(instance: String): ReactiveKLogger {
        return makeReactiveKLogger(
            instance,
            createKLogger(instances[instance]?.logger, ReactiveKLogger::class.java)
        )
    }

    override fun makeReactiveKLogger(instance: String, logger: String): ReactiveKLogger {
        return makeReactiveKLogger(
            instance,
            createKLogger(logger, ReactiveKLogger::class.java)
        )
    }

    override fun makeReactiveKLogger(instance: String, logger: Class<*>): ReactiveKLogger {
        return makeReactiveKLogger(
            instance,
            createKLogger(logger, ReactiveKLogger::class.java)
        )
    }

    override fun makeReactiveKLogger(instance: String, logger: KLogger): ReactiveKLogger {
        return ReactiveKLogger.getLogger(
            logger,
            instances[instance]?.contextKey,
            instances[instance]?.scheduler?.toScheduler()
        )
    }

    override fun makeCoroutineLogger(instance: String): CoroutineLogger {
        return makeCoroutineLogger(
            instance,
            createLogger(instances[instance]?.logger, CoroutineLogger::class.java)
        )
    }

    override fun makeCoroutineLogger(instance: String, logger: String): CoroutineLogger {
        return makeCoroutineLogger(
            instance,
            createLogger(logger, CoroutineLogger::class.java)
        )
    }

    override fun makeCoroutineLogger(instance: String, logger: Class<*>): CoroutineLogger {
        return makeCoroutineLogger(
            instance,
            createLogger(logger, CoroutineLogger::class.java)
        )
    }

    override fun makeCoroutineLogger(instance: String, logger: Logger): CoroutineLogger {
        return CoroutineLogger.getLogger(
            logger,
            instances[instance]?.contextKey,
            instances[instance]?.scheduler?.toScheduler()
        )
    }

    override fun makeCoroutineKLogger(instance: String): CoroutineKLogger {
        return makeCoroutineKLogger(
            instance,
            createKLogger(instances[instance]?.logger, CoroutineKLogger::class.java),
        )
    }

    override fun makeCoroutineKLogger(instance: String, logger: String): CoroutineKLogger {
        return makeCoroutineKLogger(
            instance,
            createKLogger(logger, CoroutineKLogger::class.java)
        )
    }

    override fun makeCoroutineKLogger(instance: String, logger: Class<*>): CoroutineKLogger {
        return makeCoroutineKLogger(
            instance,
            createKLogger(logger, CoroutineKLogger::class.java)
        )
    }

    override fun makeCoroutineKLogger(instance: String, logger: KLogger): CoroutineKLogger {
        return CoroutineKLogger.getLogger(
            logger,
            instances[instance]?.contextKey,
            instances[instance]?.scheduler?.toScheduler()
        )
    }
}