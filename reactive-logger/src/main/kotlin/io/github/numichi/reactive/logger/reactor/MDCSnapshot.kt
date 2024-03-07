package io.github.numichi.reactive.logger.reactor

import org.slf4j.MDC

class MDCSnapshot private constructor(context: Map<String, String?>?) : AutoCloseable {
    companion object {
        @JvmStatic
        fun of(context: Map<String, String?>?): MDCSnapshot {
            return MDCSnapshot(context)
        }

        @JvmStatic
        fun empty(): MDCSnapshot {
            return MDCSnapshot(null)
        }
    }

    init {
        if (context == null) {
            close()
        } else {
            MDC.setContextMap(context)
        }
    }

    val copyOfContextMap: Map<String, String?>
        get() = MDC.getCopyOfContextMap()

    override fun close() {
        MDC.clear()
    }
}
