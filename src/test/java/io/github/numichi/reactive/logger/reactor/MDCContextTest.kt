package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.DefaultValues
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.exception.InvalidContextDataException
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

internal class MDCContextTest {
    @Test
    fun `should give the MDC you are looking for`() {
        val mdc1 = MDC()
        mdc1["mdcKey"] = "mdcValue"

        val mdc2 = MDC(ANOTHER_CONTEXT_KEY)
        mdc2["mdcKey"] = "mdcValue"

        val resultDefaultByContext: Mono<MDC> = Mono.deferContextual { MDCContext.read(it) }
            .contextWrite { MDCContext.put(it, mdc1) }
        StepVerifier.create(resultDefaultByContext)
            .expectNext(mdc1)
            .verifyComplete()

        val resultDefault: Mono<MDC> = MDCContext.read()
            .contextWrite { MDCContext.put(it, mdc1) }
        StepVerifier.create(resultDefault)
            .expectNext(mdc1)
            .verifyComplete()

        val resultAnotherByContext: Mono<MDC> = Mono.deferContextual { MDCContext.read(it, ANOTHER_CONTEXT_KEY) }
            .contextWrite { MDCContext.put(it, mdc2) }
        StepVerifier.create(resultAnotherByContext)
            .expectNext(mdc2)
            .verifyComplete()

        val resultAnother: Mono<MDC> = MDCContext.read(ANOTHER_CONTEXT_KEY)
            .contextWrite { MDCContext.put(it, mdc2) }
        StepVerifier.create(resultAnother)
            .expectNext(mdc2)
            .verifyComplete()
    }

    @Test
    fun `should return the appropriate contexts count`() {
        val defaultMdc = MDC()
        defaultMdc["mdcKey"] = "mdcValue"

        val anotherMdc = MDC(ANOTHER_CONTEXT_KEY)
        anotherMdc["mdcKey"] = "mdcValue"

        val contextSize1 = Mono.deferContextual { Mono.just(it.size()) }
            .contextWrite { MDCContext.put(it, defaultMdc) }
            .contextWrite { MDCContext.put(it, anotherMdc) }
        StepVerifier.create(contextSize1)
            .expectNext(2)
            .verifyComplete()

        val contextSize2 = Mono.deferContextual { Mono.just(it.size()) }
            .contextWrite { MDCContext.put(it, defaultMdc, anotherMdc) }
        StepVerifier.create(contextSize2)
            .expectNext(2)
            .verifyComplete()

        val contextSize3 = Mono.deferContextual { Mono.just(it.size()) }
            .contextWrite { MDCContext.put(it, defaultMdc, null) }
        StepVerifier.create(contextSize3)
            .expectNext(1)
            .verifyComplete()

        val contextSize4 = Mono.deferContextual { Mono.just(it.size()) }
            .contextWrite { MDCContext.put(it, defaultMdc, null) }
            .contextWrite { it.put("A", "B") }
        StepVerifier.create(contextSize4)
            .expectNext(2)
            .verifyComplete()
    }

    @Test
    fun `should write within writeContext`() {
        val data1 = MDCContext.read()
            .mapNotNull { it["key"] }
            .contextWrite {
                val mdc = MDCContext.getMDCOrDefault(it)
                mdc["key"] = "example"
                MDCContext.put(it, mdc)
            }
        StepVerifier.create(data1)
            .expectNext("example")
            .verifyComplete()

        val data2 = MDCContext.read("another")
            .mapNotNull { it["key"] }
            .contextWrite {
                val mdc = MDCContext.getMDCOrDefault(it, "another")
                mdc["key"] = "example"
                MDCContext.put(it, mdc)
            }
        StepVerifier.create(data2)
            .expectNext("example")
            .verifyComplete()

        val data3 = MDCContext.read()
            .mapNotNull { it["key"] }
            .contextWrite {
                var mdc = MDCContext.getMDCOrNull(it)
                if (mdc == null) {
                    mdc = MDC()
                }

                mdc["key"] = "example"
                MDCContext.put(it, mdc)
            }
        StepVerifier.create(data3)
            .expectNext("example")
            .verifyComplete()

        val data4 = MDCContext.read("another")
            .mapNotNull { it["key"] }
            .contextWrite {
                var mdc = MDCContext.getMDCOrNull(it, "another")

                if (mdc == null) {
                    mdc = MDC("another")
                    mdc!!["key"] = "example"
                }

                MDCContext.put(it, mdc)
            }
        StepVerifier.create(data4)
            .expectNext("example")
            .verifyComplete()
    }

    @Test
    fun `should give the MDC you are looking for (more MDC Context)`() {
        val defaultMdc = MDC()
        defaultMdc["mdcKey"] = "mdcValue"
        val anotherMdc = MDC(ANOTHER_CONTEXT_KEY)
        anotherMdc["mdcKey"] = "mdcValue"

        val resultDefault: Mono<MDC> = Mono.defer { MDCContext.read() }
            .contextWrite { MDCContext.put(it, defaultMdc) }
            .contextWrite { MDCContext.put(it, anotherMdc) }
        StepVerifier.create(resultDefault)
            .expectNext(defaultMdc)
            .verifyComplete()

        val resultAnother: Mono<MDC> = Mono.defer { MDCContext.read(ANOTHER_CONTEXT_KEY) }
            .contextWrite { MDCContext.put(it, defaultMdc) }
            .contextWrite { MDCContext.put(it, anotherMdc) }
        StepVerifier.create(resultAnother)
            .expectNext(anotherMdc)
            .verifyComplete()
    }

    @Test
    fun `should throw InvalidContextDataException if context data is invalid`() {
        val mdc = MDC(ANOTHER_CONTEXT_KEY)
        mdc["mdcKey2"] = "mdcValue2"

        val result1: Mono<MDC> = Mono.defer { MDCContext.read() }
            .contextWrite { MDCContext.put(it, mdc) }
        StepVerifier.create(result1)
            .expectError(InvalidContextDataException::class.java)
            .verify()


        val result2: Mono<MDC> = Mono.defer { MDCContext.read("not-exist-context-id") }
            .contextWrite { MDCContext.put(it!!, mdc) }
        StepVerifier.create(result2)
            .expectError(InvalidContextDataException::class.java)
            .verify()

        val result3: Mono<MDC> = Mono.defer { MDCContext.read() }
            .contextWrite { it.put(DefaultValues.getInstance().defaultReactorContextMdcKey, "") }
        StepVerifier.create(result3)
            .expectError(InvalidContextDataException::class.java)
            .verify()

        val result4: Mono<MDC> = Mono.defer { MDCContext.read() }
            .contextWrite { it.put(DefaultValues.getInstance().defaultReactorContextMdcKey, 100) }
        StepVerifier.create(result4)
            .expectError(InvalidContextDataException::class.java)
            .verify()
    }

    companion object {
        const val ANOTHER_CONTEXT_KEY = "another-context-key"
    }
}