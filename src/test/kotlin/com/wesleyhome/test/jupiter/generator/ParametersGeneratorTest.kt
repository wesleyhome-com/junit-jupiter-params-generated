package com.wesleyhome.test.jupiter.generator

import com.wesleyhome.test.jupiter.InvalidParameterException
import com.wesleyhome.test.jupiter.annotations.*
import com.wesleyhome.test.jupiter.kotlin.TestKotlinEnum
import com.wesleyhome.test.jupiter.kotlin.TestObjectKotlin
import com.wesleyhome.test.jupiter.provider.TestModel
import com.wesleyhome.test.jupiter.provider.TestParameter
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.KClass

class ParametersGeneratorTest {

  @Test
  fun arguments_enum_bool_class() {
    assertTestParameterSourceMethod(
      TestKotlinEnum.values().size * 2,
      "testMethod",
      TestKotlinEnum::class, Boolean::class
    )
  }

  @Test
  fun arguments_enum_bool_obj_error_class() {
    assertThatThrownBy {
      assertTestParameterSourceMethod(
        0,
        "testMethodError",
        TestKotlinEnum::class, Boolean::class, TestObjectKotlin::class
      )
    }.isInstanceOf(InvalidParameterException::class.java)
      .hasMessage("Unable to find a suitable data provider for '${TestObjectKotlin::class.qualifiedName}' type")
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
      true,
      TestKotlinEnum::class
    )
  }

  @Test
  fun provideArguments_boolean() {
    assertTestParameterSourceMethod(2, "testBooleanMethod", Boolean::class)
  }

  @Test
  fun provideArguments_int() {
    assertTestParameterSourceMethod(
      2, "testIntMethod", TestParameter(
        name = "intInput",
        type = Int::class,
        annotations = listOf(IntSource(intArrayOf(10, 20)))
      )
    )
  }

  @Test
  fun provideArguments_int_range() {
    assertTestParameterSourceMethod(
      11, "testIntMethod", TestParameter(
        name = "intInput",
        type = Int::class,
        annotations = listOf(IntRangeSource(10, 20))
      )
    )
  }

  @Test
  fun provideArguments_int_range_increment() {
    assertTestParameterSourceMethod(
      6, "testIntMethod", TestParameter(
        name = "intInput",
        type = Int::class,
        annotations = listOf(IntRangeSource(10, 20, 2))
      )
    )
  }

  @Test
  fun provideArguments_double() {
    assertTestParameterSourceMethod(
      2, "testDoubleMethod", TestParameter(
        name = "doubleInput",
        type = Double::class,
        annotations = listOf(DoubleSource(doubleArrayOf(10.0, 20.0)))
      )
    )
  }

  @Test
  fun provideArguments_double_range() {
    assertTestParameterSourceMethod(
      21, "testDoubleMethod", TestParameter(
        name = "doubleInput",
        type = Double::class,
        annotations = listOf(DoubleRangeSource(10.0, 20.0))
      )
    )
  }

  @Test
  fun provideArguments_double_range_increment() {
    assertTestParameterSourceMethod(
      5, "testDoubleMethod", TestParameter(
        name = "doubleInput",
        type = Double::class,
        annotations = listOf(DoubleRangeSource(10.0, 20.0, 2.5))
      )
    )
  }

  @Test
  fun provideArguments_float() {
    assertTestParameterSourceMethod(
      2, "testFloatMethod", TestParameter(
        name = "floatInput",
        type = Float::class,
        annotations = listOf(FloatSource(floatArrayOf(10f, 20f)))
      )
    )
  }

  @Test
  fun provideArguments_float_range() {
    assertTestParameterSourceMethod(
      21, "testFloatMethod", TestParameter(
        name = "floatInput",
        type = Float::class,
        annotations = listOf(FloatRangeSource(10f, 20f))
      )
    )
  }

  @Test
  fun provideArguments_float_range_increment() {
    assertTestParameterSourceMethod(
      5, "testFloatMethod", TestParameter(
        name = "floatInput",
        type = Float::class,
        annotations = listOf(FloatRangeSource(10f, 20f, 2.5f))
      )
    )
  }

  @Test
  fun provideArguments_long() {
    assertTestParameterSourceMethod(
      2, "testLongMethod", TestParameter(
        name = "longInput",
        type = Long::class,
        annotations = listOf(LongSource(longArrayOf(10, 20)))
      )
    )
  }

  @Test
  fun provideArguments_long_range() {
    assertTestParameterSourceMethod(
      11, "testBooleanMethod", TestParameter(
        name = "intInput",
        type = Long::class,
        annotations = listOf(LongRangeSource(10, 20))
      )
    )
  }

  @Test
  fun provideArguments_long_range_increment() {
    assertTestParameterSourceMethod(
      6, "testBooleanMethod", TestParameter(
        name = "intInput",
        type = Long::class,
        annotations = listOf(LongRangeSource(10, 20, 2))
      )
    )
  }

  @Test
  fun provideArguments_localdate_no_format() {
    assertTestParameterSourceMethod(
      2, "testLocalDateMethod", TestParameter(
        name = "localDateInput",
        type = LocalDate::class,
        annotations = listOf(LocalDateSource(values = arrayOf("2022-01-01", "2023-01-01")))
      )
    )
  }

  @Test
  fun provideArguments_localdate_with_format() {
    assertTestParameterSourceMethod(
      2, "testLocalDateMethod", TestParameter(
        name = "localDateInput",
        type = LocalDate::class,
        annotations = listOf(LocalDateSource(values = arrayOf("01/01/2022", "01/01/2023"), dateFormat = "MM/dd/yyyy"))
      )
    )
  }

  @Test
  fun provideArguments_localdate_range() {
    val localDateRangeSource = LocalDateRangeSource("2022-01-01", "2022-01-31")
    assertTestParameterSourceMethod(
      31, "testLocalDateMethod", TestParameter(
        name = "localDateInput",
        type = LocalDate::class,
        annotations = listOf(localDateRangeSource)
      )
    )
  }

  @Test
  fun provideArguments_localdate_range_increment() {
    assertTestParameterSourceMethod(
      7, "testLocalDateMethod", TestParameter(
        name = "localDateInput",
        type = LocalDate::class,
        annotations = listOf(LocalDateRangeSource("2022-01-01", "2022-01-31", increment = "P5d"))
      )
    )
  }

  @Test
  fun provideArguments_localdate_range_withFormat() {
    assertTestParameterSourceMethod(
      31, "testLocalDateMethod", TestParameter(
        name = "localDateInput",
        type = LocalDate::class,
        annotations = listOf(LocalDateRangeSource("01/01/2022", "01/31/2022", dateFormat = "MM/dd/yyyy"))
      )
    )
  }

  @Test
  fun provideArguments_localdate_range_increment_withFormat() {
    assertTestParameterSourceMethod(
      7, "testLocalDateMethod", TestParameter(
        name = "localDateInput",
        type = LocalDate::class,
        annotations = listOf(
          LocalDateRangeSource(
            "01/01/2022",
            "01/31/2022",
            increment = "P5d",
            dateFormat = "MM/dd/yyyy"
          )
        )
      )
    )
  }


  @Test
  fun provideArguments_localdatetime_no_format() {
    assertTestParameterSourceMethod(
      2, "testLocalDateTimeMethod", TestParameter(
        name = "localDateTimeInput",
        type = LocalDateTime::class,
        annotations = listOf(LocalDateTimeSource(values = arrayOf("2022-01-01 12:30", "2022-01-01 13:30")))
      )
    )
  }

  @Test
  fun provideArguments_localdatetime_with_format() {
    assertTestParameterSourceMethod(
      2, "testLocalDateTimeMethod", TestParameter(
        name = "localDateTimeInput",
        type = LocalDateTime::class,
        annotations = listOf(
          LocalDateTimeSource(
            values = arrayOf("01/01/2022 12:30 AM", "01/01/2022 1:30 PM"),
            dateTimeFormat = "MM/dd/yyyy h:mm a"
          )
        )
      )
    )
  }

  @Test
  fun provideArguments_localdatetime_range() {
    assertTestParameterSourceMethod(
      8, "testLocalDateTimeMethod", TestParameter(
        name = "localDateTimeInput",
        type = LocalDateTime::class,
        annotations = listOf(LocalDateTimeRangeSource("2022-01-01 12:30", "2022-01-01 19:30"))
      )
    )
  }

  @Test
  fun provideArguments_localdatetime_range_increment() {
    assertTestParameterSourceMethod(
      15, "testLocalDateTimeMethod", TestParameter(
        name = "localDateTimeInput",
        type = LocalDateTime::class,
        annotations = listOf(LocalDateTimeRangeSource("2022-01-01 12:30", "2022-01-01 19:30", increment = "PT30m"))
      )
    )
  }

  @Test
  fun provideArguments_localdatetime_range_withFormat() {
    assertTestParameterSourceMethod(
      8, "testLocalDateTimeMethod", TestParameter(
        name = "localDateTimeInput",
        type = LocalDateTime::class,
        annotations = listOf(
          LocalDateTimeRangeSource(
            "01/01/2022 12:30",
            "01/01/2022 19:30",
            dateTimeFormat = "MM/dd/yyyy HH:mm"
          )
        )
      )
    )
  }

  @Test
  fun provideArguments_localdatetime_range_increment_withFormat() {
    assertTestParameterSourceMethod(
      15, "testLocalDateTimeMethod", TestParameter(
        name = "localDateTimeInput",
        type = LocalDateTime::class,
        annotations = listOf(
          LocalDateTimeRangeSource(
            "01/01/2022 12:30",
            "01/01/2022 19:30",
            dateTimeFormat = "MM/dd/yyyy HH:mm",
            increment = "PT30m"
          )
        )
      )
    )
  }


  private fun assertTestParameterSourceMethod(
    expectedSize: Int,
    methodName: String,
    vararg parameterClasses: KClass<*>
  ) {
    assertTestParameterSourceMethod(expectedSize, methodName, false, *parameterClasses)
  }

  private fun assertTestParameterSourceMethod(
    expectedSize: Int,
    methodName: String,
    isNullable: Boolean,
    vararg parameterClasses: KClass<*>
  ) {
    assertTestParameterSourceMethod(
      expectedSize, methodName, *parameterClasses.map {
        TestParameter(
          name = "param_${it.simpleName}",
          type = it,
          isNullable = isNullable
        )
      }.toTypedArray()
    )
  }

  private fun assertTestParameterSourceMethod(
    expectedSize: Int,
    methodName: String,
    vararg parameters: TestParameter
  ) {
    val generator = ParametersGenerator(
      testModel = TestModel(
        name = methodName,
        testParameters = parameters.toList()
      )
    )
    val arguments = generator.arguments()
    assertThat(arguments).hasSize(expectedSize)
  }
}
