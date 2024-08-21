package com.wesleyhome.test.jupiter.kotlin

import com.wesleyhome.test.jupiter.IntegerProvider
import com.wesleyhome.test.jupiter.ParametersSource
import org.junit.jupiter.params.ParameterizedTest

internal class MethodArgumentsProviderTest {
    @ParameterizedTest
    @ParametersSource
    fun testParameters(bool: Boolean?) {
        println(bool)
    }

    @ParameterizedTest
    @ParametersSource
    fun testParameters_primitive(bool: Boolean) {
        println(bool)
    }

    @ParameterizedTest
    @ParametersSource
    fun testParameters_enum(testEnum: TestKotlinEnum?) {
        println(testEnum)
    }

    @ParameterizedTest
    @ParametersSource
    fun testParameters_enum_not_nullable(testEnum: TestKotlinEnum) {
        println(testEnum)
    }

    @ParameterizedTest
    @ParametersSource
    fun testParameters_lots(testEnum: TestKotlinEnum?, wrap: Boolean?, bool: Boolean) {
        System.out.printf("%s - %s - %s%n", testEnum, wrap, bool)
    }

    @ParameterizedTest
    @ParametersSource
    fun testParameters_lots_not_nullable(testEnum: TestKotlinEnum, wrap: Boolean?, bool: Boolean) {
        System.out.printf("%s - %s - %s%n", testEnum, wrap, bool)
    }

    @ParameterizedTest
    @ParametersSource(value = [IntegerProvider::class])
    fun testParameters_provider(`val`: Int) {
        System.out.printf("%d%n", `val`)
    }

    @ParameterizedTest
    @ParametersSource(value = [IntegerProvider::class])
    fun testParameters_providers(`val`: Int, val2: Double) {
        System.out.printf("%d - %f%n", `val`, val2)
    }

    fun doubles(): List<Double> {
        return listOf(2.0, 3.2, 5.6)
    }
}
