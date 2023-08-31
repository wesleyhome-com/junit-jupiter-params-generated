package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.generator.ParametersGenerator
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.reflect.KClass
import kotlin.reflect.jvm.kotlinFunction

class ParametersSourceArgumentsProvider : ArgumentsProvider {
  override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
    val requiredTestMethod = context.requiredTestMethod.kotlinFunction!!
    val parameters = requiredTestMethod.parameters.toMutableList().also { it.removeFirst() }.toList()
    val generator = ParametersGenerator(
      testModel = TestModel(
        name = requiredTestMethod.name,
        testParameters = parameters.map {
          TestParameter(
            name = it.name ?: "param${it.index}",
            type = it.type.classifier as KClass<*>,
            isNullable = it.type.isMarkedNullable,
            annotations = it.annotations.toList()
          )
        }
      )
    )
    val arguments = generator.arguments()
    return StreamSupport.stream(arguments.spliterator(), false)
  }
}
