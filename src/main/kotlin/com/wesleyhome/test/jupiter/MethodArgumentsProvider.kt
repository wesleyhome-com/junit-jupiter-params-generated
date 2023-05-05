package com.wesleyhome.test.jupiter

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.util.stream.Stream
import java.util.stream.StreamSupport

internal class MethodArgumentsProvider : ArgumentsProvider {
  override fun provideArguments(context: ExtensionContext): Stream<out Arguments?> {
    val parametersGenerator = ParametersGenerator.create(context)
    return StreamSupport.stream(parametersGenerator.spliterator(), false)
  }
}
