package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.IntRangeSource
import com.wesleyhome.test.jupiter.annotations.LocalDateRangeSource
import com.wesleyhome.test.jupiter.kotlin.TestKotlinEnum
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class ParametersSourceArgumentsProviderTest {

    @MockK
    private lateinit var context: ExtensionContext

    @ParameterizedTest
    @CsvSource(
        "testBooleanMethod,2",
        "testEnumMethod,7",
        "testBooleanEnumMethod,14",
        "testNullBooleanEnumMethod,21",
        "testLocalDateRangeMethod,366",
        "testIntMethod, 11"
    )
    fun provideArguments(methodName: String, expectedOptions: Int) {
        val provider = ParametersSourceArgumentsProvider()
        val javaClass = this::class.java
        val method = (javaClass.methods + javaClass.declaredMethods)
            .first { it.name == methodName }
        every { context.requiredTestMethod } returns method
        every { context.requiredTestInstance } returns this
        val arguments = provider.provideArguments(context)
        assertThat(arguments).hasSize(expectedOptions)
    }

    private fun testBooleanMethod(enabled: Boolean) {}
    private fun testEnumMethod(values: TestKotlinEnum) {}
    private fun testBooleanEnumMethod(enabled: Boolean, values: TestKotlinEnum) {}
    private fun testNullBooleanEnumMethod(enabled: Boolean?, values: TestKotlinEnum) {}
    private fun testIntMethod(@IntRangeSource(min = 10, max = 20) size: Int) {}
    private fun testLocalDateRangeMethod(
        @LocalDateRangeSource(
            min = "2022-01-01",
            max = "2023-01-01"
        ) date: LocalDate
    ) {
    }

}
