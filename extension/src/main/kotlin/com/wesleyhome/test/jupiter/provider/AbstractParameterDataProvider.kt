package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.resolveTypeArgument
import kotlin.reflect.KClass

/**
 * Base for a provider that supplies values for one parameter type.
 *
 * [T] is recovered at runtime from the type argument the subclass supplies, so [providesDataFor]
 * needs no implementation: `class FooProvider : AbstractParameterDataProvider<Foo>()` claims `Foo`
 * parameters and nothing else. Intermediate classes between the subclass and this one are resolved
 * correctly, so a shared base of your own is fine.
 */
abstract class AbstractParameterDataProvider<T : Any> : ParameterDataProvider<T> {
    private val classType: KClass<T> by lazy {
        resolveTypeArgument(AbstractParameterDataProvider::class.java, 0)
    }

    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return testParameter.type == dataProviderFor()
    }

    private fun dataProviderFor(): KClass<T> {
        return classType
    }
}
