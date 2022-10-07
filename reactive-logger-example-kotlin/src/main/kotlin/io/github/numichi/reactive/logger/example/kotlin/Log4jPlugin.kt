package io.github.numichi.reactive.logger.example.kotlin

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.logging.log4j.core.Appender
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.config.Node
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.config.plugins.PluginFactory
import org.apache.logging.log4j.core.layout.AbstractStringLayout
import java.nio.charset.Charset

@Plugin(name = "ExamplePlugin", category = Node.CATEGORY, elementType = Appender.ELEMENT_TYPE)
class ExamplePlugin : AbstractStringLayout(Charset.defaultCharset()) {
    companion object {
        @JvmStatic
        @PluginFactory
        fun pluginFactory() = ExamplePlugin()
    }

    private val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    override fun toSerializable(event: LogEvent): String {
        val model = LogModel(
            event.message.formattedMessage,
            event.contextData.toMap() // <-- here goes the MDC
        )

        return mapper.writeValueAsString(model) + "\n"
    }
}

data class LogModel(
    val message: String,
    val context: Map<String, String?>
)