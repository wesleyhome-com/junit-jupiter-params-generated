//[annotations](../../../index.md)/[com.wesleyhome.test.jupiter.annotations.datetime](../index.md)/[InstantRangeSource](index.md)/[maxOffset](max-offset.md)

# maxOffset

[jvm]\
val [maxOffset](max-offset.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)

The offset for the maximum Instant value from [java.time.Instant.now](https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html#now--). For example P1D. This need to be parsable by [java.time.Period](https://docs.oracle.com/javase/8/docs/api/java/time/Period.html). This is overridden by [maxInstant](max-instant.md) if provided.
