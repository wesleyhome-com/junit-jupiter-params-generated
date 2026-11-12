package com.wesleyhome.test.jupiter.annotations.datetime

import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.provider.datetime.LocalDateTimeRangeDataProvider

/**
 * Annotation to indicate that the annotated LocalDateTime parameter should be populated with a LocalDateTime range
 * from [min] to [max] with an [increment] step in the [ascending] direction. The default [increment] is 1 hour.
 * The default [ascending] is true. The default [dateTimeFormat] also accepts negative years, such as
 * `LocalDateTime.MIN` written as `-999999999-01-01 00:00`.
 *
 * @sample examples.LocalTimeRangeSource.example
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(LocalDateTimeRangeDataProvider::class)
@MustBeDocumented
annotation class LocalDateTimeRangeSource(
    val min: String,
    val max: String,
    val increment: String = "PT1h",
    val ascending: Boolean = true,
    val dateTimeFormat: String = "yyyy-MM-dd HH:mm"
)
