package hu.numichi.kotlin.reactive.logger

import reactor.util.context.ContextView

/**
 * @property mdcContextKey The key to the reference to MDC in a reactive context.
 * @property mdcMap A dataset that belongs to a specific context
 */
class MDC private constructor(val mdcContextKey: String, val mdcMap: MutableMap<String, String>) {
    companion object {

        /**
         * Extract the MDC context from the ReactorContext by the default key. If it does not find it, it generates an empty MDC.
         *
         * @return MDC instance
         */
        @JvmStatic
        suspend fun restore(): MDC {
            return restore(reactorContextView(), DEFAULT_REACTOR_CONTEXT_MDC_KEY)
        }

        /**
         * Extract the MDC context from the ContextView by the default key. If it does not find it, it generates an empty MDC.
         *
         * @param contextView MDC refer name
         * @return MDC instance
         */
        @JvmStatic
        fun restore(contextView: ContextView): MDC {
            return restore(contextView, DEFAULT_REACTOR_CONTEXT_MDC_KEY)
        }

        /**
         * Extract the MDC context from the ReactorContext by the mdcContextKey parameter key. If it does not find it, it generates an empty MDC.
         *
         * It is recommended for use when working with multiple MDC contexts.
         *
         * @param mdcContextKey MDC refer name
         * @return MDC instance
         */
        @JvmStatic
        suspend fun restore(mdcContextKey: String): MDC {
            return restore(reactorContextView(), mdcContextKey)
        }

        /**
         * Extract the MDC context from the ContextView by the mdcContextKey parameter key. If it does not find it, it generates an empty MDC.
         *
         * It is recommended for use when you are working with multiple MDC contexts.
         *
         * @param mdcContextKey MDC refer name
         * @param contextView A context that can contain MDC
         * @return MDC instance
         */
        @JvmStatic
        fun restore(contextView: ContextView, mdcContextKey: String): MDC {
            val def = contextView.getOrDefault(mdcContextKey, emptyMap())!!
            return MDC(mdcContextKey, def)
        }

        @JvmStatic
        private fun emptyMap(): MutableMap<String, String> = mutableMapOf()
    }
}