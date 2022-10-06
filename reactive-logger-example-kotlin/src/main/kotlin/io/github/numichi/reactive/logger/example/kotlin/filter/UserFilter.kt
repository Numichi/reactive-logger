package io.github.numichi.reactive.logger.example.kotlin.filter

import io.github.numichi.reactive.logger.coroutine.modifyMdc
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.UUID

@Component
class UserFilter : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return chain.filter(exchange)
            .contextWrite {
                it.modifyMdc { mdc -> mdc.plus(mapOf("userId" to UUID.randomUUID().toString())) }
            }
    }
}