package com.wesleyhome.test.jupiter.annotations

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class IntSource(val values: IntArray)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LongSource(val values: LongArray)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class DoubleSource(val values: DoubleArray)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class FloatSource(val values: FloatArray)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LocalDateSource(val values: Array<String>, val dateFormat: String = "yyyy-MM-dd")

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LocalDateTimeSource(val values: Array<String>, val dateTimeFormat: String = "yyyy-MM-dd HH:mm")

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class IntRangeSource(val min: Int, val max: Int, val increment: Int = 1, val ascending: Boolean = true)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LongRangeSource(val min: Long, val max: Long, val increment: Long = 1, val ascending: Boolean = true)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class DoubleRangeSource(
  val min: Double,
  val max: Double,
  val increment: Double = 0.5,
  val ascending: Boolean = true
)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class FloatRangeSource(
  val min: Float,
  val max: Float,
  val increment: Float = 0.5f,
  val ascending: Boolean = true
)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LocalDateRangeSource(
  val min: String,
  val max: String,
  val increment: String = "P1d",
  val ascending: Boolean = true,
  val dateFormat: String = "yyyy-MM-dd"
)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LocalDateTimeRangeSource(
  val min: String,
  val max: String,
  val increment: String = "PT1h",
  val ascending: Boolean = true,
  val dateTimeFormat: String = "yyyy-MM-dd HH:mm"
)
