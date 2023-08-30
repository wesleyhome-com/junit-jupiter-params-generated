package com.wesleyhome.test.jupiter.provider

import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass

data class TestModel(
  val name: String,
  val testParameters: List<TestParameter>,
  val annotations: List<Annotation> = emptyList()
)

data class TestParameter(
  val name: String,
  val type: KClass<*>,
  val isNullable: Boolean = false,
  val annotations: List<Annotation> = emptyList()
)
