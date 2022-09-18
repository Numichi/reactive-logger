package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import io.github.numichi.reactive.logger.MDC
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

internal class MDCContextTest {

    @BeforeEach
    fun setUp() {
        Configuration.reset()
    }

    @Test
    fun `should give the MDC you are looking for`() {
        run {
            val mdc = MDC()
            mdc["mdcKey"] = "mdcValue"

            val resultDefaultByContext: Mono<MDC> = Mono.deferContextual { MDCContext.read(it) }
                .contextWrite { MDCContext.put(it, mdc) }

            StepVerifier.create(resultDefaultByContext)
                .expectNext(mdc)
                .verifyComplete()

            val resultDefault: Mono<MDC> = MDCContext.read()
                .contextWrite { MDCContext.put(it, mdc) }

            StepVerifier.create(resultDefault)
                .expectNext(mdc)
                .verifyComplete()
        }

        run {
            val mdc = MDC(ANOTHER_CONTEXT_KEY)
            mdc["mdcKey"] = "mdcValue"

            val resultAnotherByContext: Mono<MDC> = Mono.deferContextual { MDCContext.read(it, ANOTHER_CONTEXT_KEY) }
                .contextWrite { MDCContext.put(it, mdc) }

            StepVerifier.create(resultAnotherByContext)
                .expectNext(mdc)
                .verifyComplete()

            val resultAnother: Mono<MDC> = MDCContext.read(ANOTHER_CONTEXT_KEY)
                .contextWrite { MDCContext.put(it, mdc) }

            StepVerifier.create(resultAnother)
                .expectNext(mdc)
                .verifyComplete()
        }
    }

    @Test
    fun `should return the appropriate contexts count`() {
        val defaultMdc = MDC()
        defaultMdc["mdcKey"] = "mdcValue"

        val anotherMdc = MDC(ANOTHER_CONTEXT_KEY)
        anotherMdc["mdcKey"] = "mdcValue"

        run {
            val mono = Mono.deferContextual { Mono.just(it.size()) }
                .contextWrite { MDCContext.put(it, defaultMdc) }
                .contextWrite { MDCContext.put(it, anotherMdc) }

            StepVerifier.create(mono)
                .expectNext(2)
                .verifyComplete()
        }

        run {
            val mono = Mono.deferContextual { Mono.just(it.size()) }
                .contextWrite { MDCContext.put(it, defaultMdc, anotherMdc) }

            StepVerifier.create(mono)
                .expectNext(2)
                .verifyComplete()
        }

        run {
            val mono = Mono.deferContextual { Mono.just(it.size()) }
                .contextWrite { MDCContext.put(it, defaultMdc, null) }

            StepVerifier.create(mono)
                .expectNext(1)
                .verifyComplete()
        }

        run {
            val mono = Mono.deferContextual { Mono.just(it.size()) }
                .contextWrite { MDCContext.put(it, defaultMdc, null) }
                .contextWrite { it.put("A", "B") }

            StepVerifier.create(mono)
                .expectNext(2)
                .verifyComplete()
        }
    }

    @Test
    fun `should write within writeContext`() {
        run {
            val data = MDCContext.read()
                .mapNotNull { it["key"] }
                .contextWrite {
                    val mdc = MDCContext.getMDCOrDefault(it)
                    mdc["key"] = "example"
                    MDCContext.put(it, mdc)
                }

            StepVerifier.create(data)
                .expectNext("example")
                .verifyComplete()
        }

        run {
            val data = MDCContext.read("another")
                .mapNotNull { it["key"] }
                .contextWrite {
                    val mdc = MDCContext.getMDCOrDefault(it, "another")
                    mdc["key"] = "example"
                    MDCContext.put(it, mdc)
                }

            StepVerifier.create(data)
                .expectNext("example")
                .verifyComplete()
        }

        run {
            val data = MDCContext.read()
                .mapNotNull { it["key"] }
                .contextWrite {
                    var mdc = MDCContext.getMDCOrNull(it)
                    if (mdc == null) {
                        mdc = MDC()
                    }

                    mdc["key"] = "example"
                    MDCContext.put(it, mdc)
                }

            StepVerifier.create(data)
                .expectNext("example")
                .verifyComplete()
        }

        run {
            val data = MDCContext.read("another")
                .mapNotNull { it["key"] }
                .contextWrite {
                    var mdc = MDCContext.getMDCOrNull(it, "another")

                    if (mdc == null) {
                        mdc = MDC("another")
                        mdc!!["key"] = "example"
                    }

                    MDCContext.put(it, mdc)
                }

            StepVerifier.create(data)
                .expectNext("example")
                .verifyComplete()
        }

        run {
            val data = MDCContext.read()
                .mapNotNull { it["key"] }
                .contextWrite {
                    MDCContext.modifyContext(it) { mdc ->
                        mdc["key"] = "example"
                    }
                }

            StepVerifier.create(data)
                .expectNext("example")
                .verifyComplete()
        }

        run {
            val data = MDCContext.read("another")
                .mapNotNull { it["key"] }
                .contextWrite {
                    MDCContext.modifyContext(it, "another") { mdc ->
                        mdc["key"] = "example"
                    }
                }

            StepVerifier.create(data)
                .expectNext("example")
                .verifyComplete()
        }

        run {
            val data = MDCContext.read()
                .mapNotNull { it["key"] }
                .contextWrite { MDCContext.modifyContext(it, mapOf("key" to "example")) }

            StepVerifier.create(data)
                .expectNext("example")
                .verifyComplete()
        }

        run {
            val data = MDCContext.read("another")
                .mapNotNull { it["key"] }
                .contextWrite { MDCContext.modifyContext(it, "another", mapOf("key" to "example")) }

            StepVerifier.create(data)
                .expectNext("example")
                .verifyComplete()
        }
    }

    @Test
    fun `should give the MDC you are looking for (more MDC Context)`() {
        val defaultMdc = MDC()
        defaultMdc["mdcKey"] = "mdcValue"
        val anotherMdc = MDC(ANOTHER_CONTEXT_KEY)
        anotherMdc["mdcKey"] = "mdcValue"

        run {
            val mono: Mono<MDC> = Mono.defer { MDCContext.read() }
                .contextWrite { MDCContext.put(it, defaultMdc) }
                .contextWrite { MDCContext.put(it, anotherMdc) }

            StepVerifier.create(mono)
                .expectNext(defaultMdc)
                .verifyComplete()
        }

        run {
            val mono: Mono<MDC> = Mono.defer { MDCContext.read(ANOTHER_CONTEXT_KEY) }
                .contextWrite { MDCContext.put(it, defaultMdc) }
                .contextWrite { MDCContext.put(it, anotherMdc) }

            StepVerifier.create(mono)
                .expectNext(anotherMdc)
                .verifyComplete()
        }
    }

    @Test
    fun `should throw InvalidContextDataException if context data is invalid`() {
        val mdc = MDC(ANOTHER_CONTEXT_KEY)
        mdc["mdcKey2"] = "mdcValue2"

        run {
            val mono: Mono<MDC> = Mono.defer { MDCContext.read() }
                .contextWrite { MDCContext.put(it, mdc) }

            StepVerifier.create(mono)
                .expectNext(MDC(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
                .verifyComplete()
        }

        run {
            val mono: Mono<MDC> = Mono.defer { MDCContext.read("not-exist-context-id") }
                .contextWrite { MDCContext.put(it!!, mdc) }

            StepVerifier.create(mono)
                .expectNext(MDC("not-exist-context-id"))
                .verifyComplete()
        }
    }

    companion object {
        const val ANOTHER_CONTEXT_KEY = "another-context-key"
    }
}