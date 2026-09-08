package com.wesleyhome.test.jupiter.annotations

import org.apiguardian.api.API
import org.apiguardian.api.API.Status.EXPERIMENTAL

/** How combinations of the generated parameter values are chosen. */
@API(status = EXPERIMENTAL, since = "4.1")
enum class GenerationStrategy {

    /** Every combination of every value. Invocations are the product of the option counts. */
    CARTESIAN,

    /**
     * Enough combinations that every pair of parameters appears with every pair of their values,
     * and no more.
     *
     * Most defects are triggered by one value or by two interacting, so this keeps most of the
     * defect-finding power of [CARTESIAN] for a fraction of the invocations - a guarantee, unlike
     * sampling. It gives up combinations that need three or more specific values at once.
     *
     * The saving comes from the number of parameters, not the number of values: with two parameters
     * it is identical to [CARTESIAN], because the single pair of parameters is the whole product.
     *
     * ```kotlin
     * @GeneratedParametersTest(strategy = PAIRWISE)
     * fun rendersEverywhere(
     *     @StringSource(["chrome", "firefox", "safari"]) browser: String,
     *     @StringSource(["windows", "mac", "linux"]) os: String,
     *     @StringSource(["en", "fr"]) locale: String
     * ) { ... }                                  // 9 invocations instead of 18
     * ```
     *
     * The pairs to cover come from the combinations the filters accept, so a pair no accepted
     * combination can contain is never demanded and a reachable one is still guaranteed to appear.
     * `examples.Pairwise` in the `examples` module works that through, with Safari only on mac.
     */
    PAIRWISE
}
