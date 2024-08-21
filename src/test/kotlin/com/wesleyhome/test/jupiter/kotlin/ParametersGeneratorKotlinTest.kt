package com.wesleyhome.test.jupiter.kotlin

import com.wesleyhome.test.jupiter.ParametersGenerator.Companion.create
import com.wesleyhome.test.jupiter.ParametersSource
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import java.util.*
import java.util.stream.Collectors
import kotlin.reflect.KClass

@ExtendWith(MockKExtension::class)
internal class ParametersGeneratorKotlinTest {

    @MockK
    private lateinit var context: ExtensionContext

    @Test
    fun provideArguments_enum_class_expectFilter() {
        assertTestParameterSourceMethod(
            (TestKotlinEnum.values().size - 1) * 2 * 5,
            "testMethodFiltered",
            TestKotlinEnum::class, Boolean::class, Int::class
        )
    }

    @Test
    fun provideArguments_enum_class() {
        assertTestParameterSourceMethod(TestKotlinEnum.values().size, "testEnumMethod", TestKotlinEnum::class)
    }

    @Test
    fun provideArguments_nullable_enum_class() {
        assertTestParameterSourceMethod(
            TestKotlinEnum.values().size + 1,
            "testNullableEnumMethod",
            TestKotlinEnum::class
        )
    }

    @Test
    fun provideArguments_BigBoolean() {
        assertTestParameterSourceMethod(2, "testBooleanMethod", Boolean::class)
    }

    @Test
    fun provideArguments_int() {
        assertTestParameterSourceMethod(5, "testIntMethod", Int::class)
    }

    @Test
    fun provideArguments_big_double() {
        assertTestParameterSourceMethod(7, "testDoubleMethod", Double::class)
    }

    @Test
    fun provideArguments_string() {
        assertTestParameterSourceMethod(10, "testStringMethod", String::class)
    }

    @Test
    fun provideArguments_testObject() {
        assertTestParameterSourceMethod(4, "testTestObjectMethod", TestObjectKotlin::class)
    }

    private fun assertTestParameterSourceMethod(
        expectedSize: Int,
        methodName: String,
        vararg parameterClasses: KClass<*>
    ) {
        val testClass = ParametersGeneratorKotlinTest::class.java
        every { context.requiredTestMethod } returns testClass.getDeclaredMethod(
            methodName,
            *parameterClasses.map { it.java }.toTypedArray()
        )
        every { context.requiredTestClass } returns testClass
        every { context.requiredTestInstance } returns this
        val arguments = create(context)
        assertThat(arguments).hasSize(expectedSize)
    }

    private fun testMethodFiltered_filter(testEnum: TestKotlinEnum, one: Boolean, two: Int): Boolean {
        return testEnum != TestKotlinEnum.BAD
    }

    @ParametersSource
    private fun testMethodFiltered(testEnum: TestKotlinEnum, one: Boolean, two: Int) {
    }

    @ParametersSource
    private fun testEnumMethod(testEnum: TestKotlinEnum) {
    }

    @ParametersSource
    private fun testNullableEnumMethod(testEnum: TestKotlinEnum?) {
    }

    @ParametersSource
    private fun testBooleanMethod(testBool: Boolean) {
    }

    @ParametersSource
    private fun testLittleBooleanMethod(testBool: Boolean) {
    }

    @ParametersSource
    private fun testIntMethod(testValue: Int) {
    }

    @ParametersSource
    private fun testDoubleMethod(testValue: Double) {
    }

    @ParametersSource
    private fun testStringMethod(testValue: String) {
    }

    @ParametersSource
    private fun testTestObjectMethod(testValue: TestObjectKotlin) {
    }

    private fun intSource(): List<Int> {
        return listOf(1, 2, 3, 4, 5)
    }

    private fun doubleSource(): List<Double> {
        return listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0)
    }

    private fun stringSource(): List<String> {
        return listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0).stream().map { o: Int? -> Objects.toString(o) }
            .collect(Collectors.toList())
    }

    private fun testObjectSource(): List<TestObjectKotlin> {
        return listOf("one", "two", "three", "four").stream()
            .map { TestObjectKotlin(it) }
            .collect(Collectors.toList())
    }
}
