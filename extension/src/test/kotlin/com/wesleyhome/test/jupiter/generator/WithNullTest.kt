package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.WithNull
import com.wesleyhome.test.jupiter.annotations.number.IntSource
import com.wesleyhome.test.jupiter.java.nullability.JavaWithNullFixture
import com.wesleyhome.test.jupiter.testkit.executionFailure
import com.wesleyhome.test.jupiter.testkit.invocationNames
import org.junit.jupiter.api.Test

/**
 * Null generation used to be inferred from Kotlin type nullability alone, which Java cannot express
 * - a Java `Integer` is nullable and never received a null case.
 */
class WithNullTest {

    @Test
    fun testAnnotationAddsANullCase() {
        assertThat(invocationNames(Fixture::class.java, "annotated")).isEqualTo(listOf("1", "2", "null"))
    }

    @Test
    fun testWithoutTheAnnotationThereIsNoNullCase() {
        assertThat(invocationNames(Fixture::class.java, "plain")).isEqualTo(listOf("1", "2"))
    }

    /** Kotlin nullability keeps working, so tests written before the annotation are unaffected. */
    @Test
    fun testKotlinNullableTypeStillInfersANullCase() {
        assertThat(invocationNames(Fixture::class.java, "kotlinNullable")).isEqualTo(listOf("1", "2", "null"))
    }

    @Test
    fun testAnnotationAndNullableTypeTogetherAddOneNullCase() {
        assertThat(invocationNames(Fixture::class.java, "bothAnnotatedAndNullable"))
            .isEqualTo(listOf("1", "2", "null"))
    }

    @Test
    fun testAnnotationOnAPrimitiveIsRejected() {
        assertThat(executionFailure(Fixture::class.java, "onAPrimitive"))
            .isInstanceOf(IllegalArgumentException::class)
            .hasMessage("@WithNull cannot be applied to [value]: int cannot hold null")
    }

    @Test
    fun testJavaBoxedParameterGetsANullCaseFromTheAnnotation() {
        assertThat(invocationNames(JavaWithNullFixture::class.java, "boxedWithAnnotation"))
            .isEqualTo(listOf("1", "2", "null"))
    }

    /** The gap this annotation closes: a Java Integer is nullable and never inferred a null case. */
    @Test
    fun testJavaBoxedParameterWithoutTheAnnotationGetsNoNullCase() {
        assertThat(invocationNames(JavaWithNullFixture::class.java, "boxedWithoutAnnotation"))
            .isEqualTo(listOf("1", "2"))
    }

    @Test
    fun testJavaPrimitiveWithTheAnnotationIsRejected() {
        assertThat(executionFailure(JavaWithNullFixture::class.java, "primitiveWithAnnotation"))
            .isInstanceOf(IllegalArgumentException::class)
            .hasMessage("@WithNull cannot be applied to [value]: int cannot hold null")
    }

    class Fixture {

        @GeneratedParametersTest(name = "{arguments}")
        fun annotated(@IntSource([1, 2]) @WithNull value: Int?) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun plain(@IntSource([1, 2]) value: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun kotlinNullable(@IntSource([1, 2]) value: Int?) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun bothAnnotatedAndNullable(@IntSource([1, 2]) @WithNull value: Int?) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun onAPrimitive(@IntSource([1, 2]) @WithNull value: Int) {
        }
    }
}
