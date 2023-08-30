package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.generator.ParametersGenerator
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.util.Spliterator
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.streams.toList

class ParametersSourceArgumentsProvider : ArgumentsProvider {
  override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
    val generator = ParametersGenerator(
      testMethodName = context.requiredTestMethod.name,
      testObject = context.requiredTestInstance,
      testModel = TestModel(
        name = context.requiredTestMethod.name,
        testParameters = context.requiredTestMethod.parameters.map {
          TestParameter(
            name = it.name,
            type = it.type.kotlin,
            annotations = it.annotations.toList()
          )
        }
      )
    )
    val arguments = generator.arguments()
    return StreamSupport.stream(arguments.spliterator(), false)
  }
}
