package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.exceptions.ReadException
import io.github.numichi.reactive.logger.stepVerifier
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.util.context.Context
import reactor.util.function.Tuples

internal class MDCContextTest {

    @BeforeEach
    fun setUp() {
        Configuration.reset()
    }

    @Test
    fun putTest() {
        run {
            var context = Context.empty()
            context = MDCContext.put(context, MDC())
            assertEquals(mapOf<String, String?>(), context.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
        }

        run {
            var context = Context.empty()
            context = MDCContext.put(context, MDC(), MDC("foo"))

            assertEquals(mapOf<String, String?>(), context.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
            assertEquals(mapOf<String, String?>(), context.get<Map<String, String?>>("foo"))
        }

        run {
            var context = Context.empty()
            context = MDCContext.put(context, MDC("111" to "222"), MDC("foo", "333" to "444"))

            assertEquals(mapOf("111" to "222"), context.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
            assertEquals(mapOf("333" to "444"), context.get<Map<String, String?>>("foo"))
        }

        run {
            assertThrows<IllegalStateException> {
                MDCContext.put(Context.empty(), MDC(), MDC())
            }
        }
    }

    @Test
    fun `read -- should give an error if not found key or the content type is not Map`() {
        val mono1 = Mono.defer { MDCContext.read() }
        val mono2 = Mono.defer { MDCContext.read(DEFAULT_REACTOR_CONTEXT_MDC_KEY) }
        val mono3 = Mono.defer { MDCContext.read("foo") }
        val mono4 = Mono.defer { MDCContext.read("not-found-key") }
            .contextWrite { it.put("another", mapOf("foo" to "bar")) }
        val mono5 = Mono.defer { MDCContext.read() }
            .contextWrite { it.put(DEFAULT_REACTOR_CONTEXT_MDC_KEY, MDC()) }

        StepVerifier.create(mono1)
            .expectErrorMatches { it.message == "DEFAULT_REACTOR_CONTEXT_MDC_KEY context key is not contain in context" }
            .verify()
        StepVerifier.create(mono2)
            .expectErrorMatches { it.message == "DEFAULT_REACTOR_CONTEXT_MDC_KEY context key is not contain in context" }
            .verify()
        StepVerifier.create(mono3)
            .expectErrorMatches { it.message == "foo context key is not contain in context" }
            .verify()
        StepVerifier.create(mono4)
            .expectErrorMatches { it.message == "not-found-key context key is not contain in context" }
            .verify()
        StepVerifier.create(mono5)
            .expectErrorMatches { it.message == "The content type is not java.util.Map<Object, Object>" }
            .verify()
    }

    @Test
    fun `read -- should give MDC mono if found context key`() {
        run {
            val ctx = Context.of(DEFAULT_REACTOR_CONTEXT_MDC_KEY, mapOf("foo" to "bar"))
            val mdc = MDCContext.read(ctx)
            val expected = MDC(DEFAULT_REACTOR_CONTEXT_MDC_KEY, mapOf("foo" to "bar"))

            assertEquals(expected, mdc)
        }

        run {
            val mono = Mono.defer { MDCContext.read() }
                .contextWrite { it.put(DEFAULT_REACTOR_CONTEXT_MDC_KEY, mapOf("foo" to "bar")) }

            StepVerifier.create(mono)
                .expectNext(MDC(DEFAULT_REACTOR_CONTEXT_MDC_KEY, mapOf("foo" to "bar")))
                .verifyComplete()
        }

        run {
            val mono = Mono.defer { MDCContext.read("baz") }
                .contextWrite { it.put("baz", mapOf("foo" to "bar")) }

            StepVerifier.create(mono)
                .expectNext(MDC("baz", mapOf("foo" to "bar")))
                .verifyComplete()
        }
    }

    @Test
    fun `readOrDefault -- should give an default MDC if not found key or the content type is not Map`() {
        stepVerifier(MDC()) { MDCContext.readOrDefault() }
        stepVerifier(MDC()) { MDCContext.readOrDefault(DEFAULT_REACTOR_CONTEXT_MDC_KEY) }
        stepVerifier(MDC("foo")) { MDCContext.readOrDefault("foo") }

        assertEquals(MDC(), MDCContext.readOrDefault(Context.empty()))
        assertEquals(MDC("foo"), MDCContext.readOrDefault(Context.empty(), "foo"))
        assertEquals(MDC("foo", mapOf("100" to "100")), MDCContext.readOrDefault(Context.of("foo", mapOf(100 to 100)), "foo"))

        assertThrows<ReadException> {
            val illegalMdcContext = Context.of("foo", MDC())
            MDCContext.readOrDefault(illegalMdcContext, "foo")
        }
    }

    @Test
    fun `readOrDefault -- should give MDC mono if found context key`() {
        run {
            val ctx = Context.of(DEFAULT_REACTOR_CONTEXT_MDC_KEY, mapOf("foo" to "bar"))
            val mdc = MDCContext.readOrDefault(ctx)
            val expected = MDC(DEFAULT_REACTOR_CONTEXT_MDC_KEY, mapOf("foo" to "bar"))

            assertEquals(expected, mdc)
        }

        run {
            val mono = Mono.defer { MDCContext.readOrDefault() }
                .contextWrite { it.put(DEFAULT_REACTOR_CONTEXT_MDC_KEY, mapOf("foo" to "bar")) }

            StepVerifier.create(mono)
                .expectNext(MDC(DEFAULT_REACTOR_CONTEXT_MDC_KEY, mapOf("foo" to "bar")))
                .verifyComplete()
        }

        run {
            val mono = Mono.defer { MDCContext.readOrDefault("baz") }
                .contextWrite { it.put("baz", mapOf("foo" to "bar")) }

            StepVerifier.create(mono)
                .expectNext(MDC("baz", mapOf("foo" to "bar")))
                .verifyComplete()
        }

        run {
            val mono = MDCContext.readOrDefault("foo")
                .contextWrite { Context.of("foo", MDC()) }

            StepVerifier.create(mono).verifyError(ReadException::class.java)
        }
    }

    @Test
    fun `merge -- merge both MDC information`() {
        // merge MAP into empty Context (and with default context key)
        run {
            val add = mapOf("111" to "222")
            val ctx = Context.empty()
            val expected = mapOf("111" to "222")

            val mdcMap1 = MDCContext.modify(ctx, add)
            assertEquals(expected, mdcMap1.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))

            val mdcMap2 = MDCContext.modify(ctx, "foo", add)
            assertEquals(expected, mdcMap2.get<Map<String, String?>>("foo"))
        }

        // merge MAP into Context (with default context key)
        run {
            val add = mapOf("111" to "222")
            val ctx = Context.of(DEFAULT_REACTOR_CONTEXT_MDC_KEY, mapOf("foo" to "bar"))
            val expected = mapOf("foo" to "bar", "111" to "222")

            val mdcMap1 = MDCContext.modify(ctx, add)
            assertEquals(expected, mdcMap1.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
        }

        // merge MAP into Context
        run {
            val add = mapOf("111" to "222")
            val ctx = Context.of("foo", mapOf("foo" to "bar"))
            val expected = mapOf("foo" to "bar", "111" to "222")

            val mdcMap2 = MDCContext.modify(ctx, "foo", add)
            assertEquals(expected, mdcMap2.get<Map<String, String?>>("foo"))
        }

        // merge Tuple into Context
        run {
            val add = Tuples.of("111", "222")
            val ctx = Context.of("foo", mapOf("foo" to "bar"))
            val expected1 = mapOf("111" to "222")
            val expected2 = mapOf("foo" to "bar", "111" to "222")

            val mdcMap1 = MDCContext.modify(ctx, add)
            val mdcMap2 = MDCContext.modify(ctx, "foo", add)
            assertEquals(expected1, mdcMap1.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
            assertEquals(expected2, mdcMap2.get<Map<String, String?>>("foo"))
        }

        // merge Pair into Context
        run {
            val add = "111" to "222"
            val ctx = Context.of("foo", mapOf("foo" to "bar"))
            val expected1 = mapOf("111" to "222")
            val expected2 = mapOf("foo" to "bar", "111" to "222")

            val mdcMap1 = MDCContext.modify(ctx, add)
            val mdcMap2 = MDCContext.modify(ctx, "foo", add)
            assertEquals(expected1, mdcMap1.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
            assertEquals(expected2, mdcMap2.get<Map<String, String?>>("foo"))
        }

        // merge MDC.of object into Context
        run {
            val add = MDC("foo", "111" to "222")
            val ctx = Context.of("foo", mapOf("foo" to "bar"))
            val expected = mapOf("foo" to "bar", "111" to "222")

            val mdcMap2 = MDCContext.modify(ctx, add)
            assertEquals(expected, mdcMap2.get<Map<String, String?>>("foo"))
        }
    }

    @Test
    fun `modify -- modify Context`() {
        run {
            var ctx = Context.of(DEFAULT_REACTOR_CONTEXT_MDC_KEY, mapOf("foo" to "bar"))
            ctx = MDCContext.modify(ctx) { it.plus(mapOf("111" to "222")) }

            val expected = mapOf("111" to "222", "foo" to "bar")

            assertEquals(expected, ctx.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
        }

        run {
            var ctx = Context.of("foo", mapOf("foo" to "bar"))
            ctx = MDCContext.modify(ctx, "foo") { it.plus(mapOf("111" to "222")) }

            val expected = mapOf("111" to "222", "foo" to "bar")

            assertEquals(expected, ctx.get<Map<String, String?>>("foo"))
        }
    }

    @Test
    fun `snapshot -- create snapshot from MDC`() {
        run {
            val snapshot1 = MDCContext.snapshot()

            StepVerifier.create(snapshot1)
                .expectNext(MDC())
                .verifyComplete()


            val snapshot2 = MDCContext.snapshot()
                .contextWrite {
                    MDCContext.modify(it) { mdc ->
                        mdc + ("foo" to "bar")
                    }
                }

            StepVerifier.create(snapshot2)
                .expectNext(MDC("foo" to "bar"))
                .verifyComplete()
        }

        run {
            val snapshot1 = MDCContext.snapshot("foo")

            StepVerifier.create(snapshot1)
                .expectNext(MDC("foo"))
                .verifyComplete()


            val snapshot2 = MDCContext.snapshot("foo")
                .contextWrite {
                    MDCContext.modify(it, "foo") { mdc ->
                        mdc + ("foo" to "bar")
                    }
                }

            StepVerifier.create(snapshot2)
                .expectNext(MDC("foo", "foo" to "bar"))
                .verifyComplete()
        }

        run {
            val context = Context.of(
                mapOf(
                    DEFAULT_REACTOR_CONTEXT_MDC_KEY to mapOf("bar" to "baz"),
                    "foo" to mapOf("bar1" to "baz1")
                )
            )

            val snapshot1 = MDCContext.snapshot(context)
            val snapshot2 = MDCContext.snapshot(context, "foo")

            assertEquals(MDC("bar" to "baz"), snapshot1)
            assertEquals(MDC("foo", "bar1" to "baz1"), snapshot2)
        }
    }

    @Test
    fun `snapshot -- create snapshot from MDC and `() {
        Configuration.addHook("name", "aaa") { value, mdc ->
            check(value is String && mdc.contextKey == "foo")
            mapOf("bbb" to value.uppercase())
        }

        val context = Context.of(
            mapOf(
                DEFAULT_REACTOR_CONTEXT_MDC_KEY to mapOf("bar" to "baz"),
                "foo" to mapOf("bar1" to "baz1"),
                "aaa" to "bbb"
            )
        )

        val snapshot1 = MDCContext.snapshot(context)
        val snapshot2 = MDCContext.snapshot(context, "foo")

        assertEquals(MDC("bar" to "baz"), snapshot1)
        assertEquals(MDC("foo", mapOf("bar1" to "baz1", "bbb" to "BBB")), snapshot2)
    }
}