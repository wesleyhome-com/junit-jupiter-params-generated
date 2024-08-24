package com.wesleyhome.test.jupiter

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass

val Any.actualTypeArguments: Array<Type>
    get() = (this.javaClass.genericSuperclass as ParameterizedType)
        .actualTypeArguments

fun <T: Any> Type.kotlinType(): KClass<T> {
    return when (this) {
        is Class<*> -> (this as Class<T>).kotlin
        is ParameterizedType -> this.rawType.kotlinType()
        else -> throw IllegalArgumentException("Unsupported type: $this")
    }
}
