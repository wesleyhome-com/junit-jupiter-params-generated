package com.wesleyhome.test.jupiter.annotations

import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.provider.ValuesFromDataProvider
import org.apiguardian.api.API
import org.apiguardian.api.API.Status.EXPERIMENTAL

/**
 * Takes a parameter's values from a function, for types no annotation can express, such as a data
 * class or a nested object. The values combine with the other parameters like any other source's.
 *
 * The function takes no arguments and returns a `List`, `Sequence` or array of the parameter's type.
 * It is found by name the way a filter is: static, or in an object or companion object, on the test
 * class, its superclasses or its interfaces, and for a `@Nested` class on the classes enclosing it.
 * If the name is overloaded, the overload that takes no arguments is used. Name it as
 * `com.example.Fixtures#ranges` to share one between test classes. The annotation processor checks
 * all of this at compile time, except that it doesn't yet look in the test class's interfaces or
 * enclosing classes; name the class there.
 *
 * A worked version, compiled and checked by the annotation processor, is `examples.ValuesFrom` in the
 * `examples` module; the README shows it in full.
 *
 * @property value the function's name, or a class and a function as `com.example.Fixtures#ranges`
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@API(status = EXPERIMENTAL, since = "4.1")
@SourceProvider(ValuesFromDataProvider::class)
annotation class ValuesFrom(val value: String)
