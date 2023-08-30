package com.wesleyhome.test.jupiter.provider

import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

abstract class AbstractParameterDataProvider<T : Any> : ParameterDataProvider<T> {

  private val classType: KClass<T>
  init {
    val parameterizedType = (this.javaClass.genericSuperclass as ParameterizedType)
    val typeArgument = parameterizedType.actualTypeArguments[0]
    val typeClass = typeArgument as Class<*>
    classType = typeClass.kotlin as KClass<T>
  }

  override fun providesDataFor(testParameter: TestParameter): Boolean {
    return testParameter.type == classType
  }

}
