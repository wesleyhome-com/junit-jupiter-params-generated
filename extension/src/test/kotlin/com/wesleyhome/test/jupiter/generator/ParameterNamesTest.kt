package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.hasMessage
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import assertk.assertions.single
import com.wesleyhome.test.jupiter.InvalidFilterException
import com.wesleyhome.test.jupiter.InvalidValuesFromException
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import com.wesleyhome.test.jupiter.consumer.JavaConsumerFilters
import com.wesleyhome.test.jupiter.consumer.JavaConsumerFixture
import com.wesleyhome.test.jupiter.consumer.KotlinConsumerFixture
import com.wesleyhome.test.jupiter.consumer.KotlinConsumerRules
import com.wesleyhome.test.jupiter.consumer.NamedCompanionConsumerFixture
import com.wesleyhome.test.jupiter.java.filter.JavaFilterFixture
import com.wesleyhome.test.jupiter.stripped.JavaStrippedFilters
import com.wesleyhome.test.jupiter.stripped.JavaStrippedFixture
import com.wesleyhome.test.jupiter.testkit.executionFailure
import com.wesleyhome.test.jupiter.testkit.invocationNames
import com.wesleyhome.test.jupiter.testkit.reportEntries
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.InputStream
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger

private val ORDERED = listOf("1, 1", "1, 2", "1, 3", "2, 2", "2, 3", "3, 3")

/**
 * This repository compiles with javaParameters and -parameters, which a consumer's build doesn't.
 * The fixtures here are compiled without them: Kotlin as it is by default, Java with Gradle's -g,
 * and Java with -g:none, which records no parameter names at all.
 */
class ParameterNamesTest {

    @Nested
    inner class KotlinWithoutJavaParameters {

        @Test
        fun testFiltersBindByTheirKotlinNames() {
            assertThat(invocationNames(KotlinConsumerFixture::class.java, "jvmStatic")).isEqualTo(ORDERED)
            assertThat(invocationNames(KotlinConsumerFixture::class.java, "companionMember")).isEqualTo(ORDERED)
            assertThat(invocationNames(KotlinConsumerFixture::class.java, "internalFilter")).isEqualTo(ORDERED)
            assertThat(invocationNames(NamedCompanionConsumerFixture::class.java, "ordered")).isEqualTo(ORDERED)
        }

        @Test
        fun testAFilterDeclaringItsParametersInAnotherOrderBindsByName() {
            assertThat(invocationNames(KotlinConsumerFixture::class.java, "reversed")).isEqualTo(ORDERED)
        }

        /** A value class mangles the filter's JVM name, to `valueClass_filter-<hash>`. */
        @Test
        fun testAFilterTakingAValueClassBindsByName() {
            assertThat(invocationNames(KotlinConsumerFixture::class.java, "valueClass")).isEqualTo(
                listOf("Cents(value=1), 2", "Cents(value=2), 1", "Cents(value=3), 1", "Cents(value=3), 2")
            )
        }

        /** A synthetic method records no parameter names of its own, but its Kotlin declaration does. */
        @Test
        fun testAJvmSyntheticFilterBindsByName() {
            assertThat(invocationNames(KotlinConsumerFixture::class.java, "synthetic")).isEqualTo(listOf("1", "2"))
        }

        @Test
        fun testMessagesNameTheKotlinParameter() {
            assertThat(executionFailure(KotlinConsumerFixture::class.java, "unknownParameter"))
                .isInstanceOf(InvalidFilterException::class)
                .hasMessage("Filter [mentionsMissing] declares [nope], which is not a generated parameter of the test")
            assertThat(executionFailure(KotlinConsumerFixture::class.java, "wrongType"))
                .isInstanceOf(InvalidFilterException::class)
                .hasMessage("Filter [expectsString] declares [start] as String, but the generated parameter is Integer")
            assertThat(executionFailure(KotlinConsumerFixture::class.java, "nullIntoPrimitive"))
                .isInstanceOf(InvalidFilterException::class)
                .hasMessage(
                    "Filter [expectsPrimitive] declares [start] as int, which cannot hold the null this parameter generates"
                )
            assertThat(executionFailure(KotlinConsumerFixture::class.java, "valuesFromMissing"))
                .isInstanceOf(InvalidValuesFromException::class)
                .hasMessage(
                    "@ValuesFrom(\"nope\") on [label]: no function named [nope] on KotlinConsumerFixture " +
                        "or its companion object"
                )
        }

        /** An extension receiver has no Kotlin name, so it's reported by the name it compiles to. */
        @Test
        fun testAnExtensionReceiverIsReportedByItsCompiledName() {
            assertThat(executionFailure(KotlinConsumerFixture::class.java, "extensionReceiver"))
                .isInstanceOf(InvalidFilterException::class)
                .hasMessage(
                    "Filter [receiverRule] declares [\$this\$receiverRule], which is not a generated parameter of the test"
                )
        }

        @Test
        fun testDisplayNamesAreUnchanged() {
            assertThat(invocationNames(KotlinConsumerFixture::class.java, "displayed")).isEqualTo(
                listOf("[1] start=1, end=1", "[2] start=1, end=2", "[3] start=2, end=1", "[4] start=2, end=2")
            )
        }

        @Test
        fun testNothingIsLogged() {
            assertThat(warningsWhile { invocationNames(KotlinConsumerFixture::class.java, "jvmStatic") }).isEmpty()
        }
    }

    @Nested
    inner class JavaWithoutParametersFlag {

        /** By position, the filter's only parameter used to take `start`. */
        @Test
        fun testAFilterOverOneParameterBindsToThatParameter() {
            assertThat(invocationNames(JavaConsumerFixture::class.java, "endNotOne"))
                .isEqualTo(listOf("1, 2", "1, 3", "2, 2", "2, 3", "3, 2", "3, 3"))
        }

        /** By position, these filters used to run exactly the combinations they reject. */
        @Test
        fun testAFilterDeclaringItsParametersInAnotherOrderBindsByName() {
            assertThat(invocationNames(JavaConsumerFixture::class.java, "ordered")).isEqualTo(ORDERED)
            assertThat(invocationNames(JavaConsumerFixture::class.java, "companionField")).isEqualTo(ORDERED)
            assertThat(invocationNames(JavaConsumerFixture::class.java, "fromInterface")).isEqualTo(ORDERED)
        }

        /** The filter's class file records no names, so it binds by position, as it always has. */
        @Test
        fun testAFilterWithoutNamesStillBindsByPosition() {
            assertThat(warningsWhile {
                assertThat(invocationNames(JavaConsumerFixture::class.java, "strippedFilter")).isEqualTo(ORDERED)
            }).containsExactly(positionalWarning("strippedOrdered"))
        }

        @Test
        fun testAFilterRepeatingTheTestsParametersInOrderStillApplies() {
            assertThat(invocationNames(JavaConsumerFixture::class.java, "inOrder")).isEqualTo(ORDERED)
        }

        /** By position, the filter's first parameter used to take the `TestInfo`, and fail. */
        @Test
        fun testAParameterAnotherExtensionResolvesDoesNotShiftTheNames() {
            assertThat(invocationNames(JavaConsumerFixture::class.java, "afterTestInfo")).isEqualTo(listOf("1", "3"))
        }

        /** A long or a double takes two local variable slots, which shifts every name after it. */
        @Test
        fun testNamesAfterALongOrADoubleAreReadFromTheRightSlot() {
            assertThat(invocationNames(JavaConsumerFixture::class.java, "wide"))
                .isEqualTo(listOf("1, 0.5, 2", "1, 0.5, 3", "2, 0.5, 3"))
        }

        /** By position, a filter naming a parameter the test doesn't have used to take the first one. */
        @Test
        fun testMessagesNameTheJavaParameter() {
            assertThat(executionFailure(JavaConsumerFixture::class.java, "unknownParameter"))
                .isInstanceOf(InvalidFilterException::class)
                .hasMessage("Filter [mentionsMissing] declares [nope], which is not a generated parameter of the test")
            assertThat(executionFailure(JavaConsumerFixture::class.java, "wrongType"))
                .isInstanceOf(InvalidFilterException::class)
                .hasMessage("Filter [expectsString] declares [start] as String, but the generated parameter is Integer")
            assertThat(executionFailure(JavaConsumerFixture::class.java, "valuesFromMissing"))
                .isInstanceOf(InvalidValuesFromException::class)
                .hasMessage(
                    "@ValuesFrom(\"nope\") on [label]: no function named [nope] on JavaConsumerFixture " +
                        "or its companion object"
                )
        }

        /** Display names and report entries still need -parameters, so they keep `arg0` in a patch release. */
        @Test
        fun testDisplayNamesAndReportEntriesAreUnchanged() {
            assertThat(invocationNames(JavaConsumerFixture::class.java, "displayed")).isEqualTo(
                listOf("[1] arg0=1, arg1=1", "[2] arg0=1, arg1=2", "[3] arg0=2, arg1=1", "[4] arg0=2, arg1=2")
            )
            assertThat(reportEntries(JavaConsumerFixture::class.java, "displayed").single()["generated.parameters"])
                .isEqualTo("arg0=2 x arg1=2")
        }

        @Test
        fun testNothingIsLogged() {
            assertThat(warningsWhile { invocationNames(JavaConsumerFixture::class.java, "ordered") }).isEmpty()
        }

        /** The combination is named as display names name it, so it says `arg0` where they do. */
        @Test
        fun testAThrowingFilterNamesTheCombinationAsDisplayNamesDo() {
            assertThat(executionFailure(JavaConsumerFixture::class.java, "throwing"))
                .isInstanceOf(InvalidFilterException::class)
                .hasMessage("Filter [throwsAtTwo] on [arg0=2, arg1=1] threw IllegalStateException: boom")
        }
    }

    @Nested
    inner class JavaWithoutAnyNames {

        @Test
        fun testFiltersStillBindByPosition() {
            assertThat(invocationNames(JavaStrippedFixture::class.java, "ordered")).isEqualTo(ORDERED)
            assertThat(invocationNames(JavaStrippedFixture::class.java, "reversed"))
                .isEqualTo(listOf("1, 1", "2, 1", "2, 2", "3, 1", "3, 2", "3, 3"))
        }

        /** The filter's names are recorded, but there are none on the test's side to match them to. */
        @Test
        fun testAFilterWithNamesStillBindsByPositionToATestWithout() {
            assertThat(invocationNames(JavaStrippedFixture::class.java, "namedFilter")).isEqualTo(ORDERED)
        }

        @Test
        fun testAParameterAnotherExtensionResolvesStillShiftsThePositions() {
            assertThat(executionFailure(JavaStrippedFixture::class.java, "afterTestInfo"))
                .isInstanceOf(InvalidFilterException::class)
                .hasMessage("Filter [afterTestInfo_filter] declares [arg0], which is not a generated parameter of the test")
        }

        @Test
        fun testABindingByPositionIsWarnedAbout() {
            assertThat(warningsWhile { invocationNames(JavaStrippedFixture::class.java, "ordered") }).single()
                .isEqualTo(positionalWarning("ordered_filter"))
            assertThat(warningsWhile { invocationNames(JavaStrippedFixture::class.java, "namedFilter") }).single()
                .isEqualTo(positionalWarning("sharedOrdered"))
        }

        /** The filter's names aren't recorded, so it binds by Java reflection's `arg0`, as it always has. */
        @Test
        fun testAFilterWithoutNamesFailsAgainstATestWithNamesAsBefore() {
            assertThat(executionFailure(StrippedFilterFixture::class.java, "ordered"))
                .isInstanceOf(InvalidFilterException::class)
                .hasMessage("Filter [strippedOrdered] declares [arg0], which is not a generated parameter of the test")
            assertThat(warningsWhile { executionFailure(StrippedFilterFixture::class.java, "ordered") })
                .containsExactly(positionalWarning("strippedOrdered"))
        }

        @Test
        fun testDisplayNamesAndMessagesAreUnchanged() {
            assertThat(invocationNames(JavaStrippedFixture::class.java, "displayed")).isEqualTo(
                listOf("[1] arg0=1, arg1=1", "[2] arg0=1, arg1=2", "[3] arg0=2, arg1=1", "[4] arg0=2, arg1=2")
            )
            assertThat(executionFailure(JavaStrippedFixture::class.java, "valuesFromMissing"))
                .isInstanceOf(InvalidValuesFromException::class)
                .hasMessage(
                    "@ValuesFrom(\"nope\") on [arg0]: no function named [nope] on JavaStrippedFixture " +
                        "or its companion object"
                )
        }
    }

    /** Each source of names is a fallback for the one before, so one that has none is skipped rather than failing. */
    @Nested
    inner class SourcesWithoutNames {

        /** A lambda's body compiles to `capturing$lambda$0`, taking the captured `$start`. */
        @Test
        fun testAMethodWithoutAKotlinDeclarationFallsBackToItsCompiledNames() {
            val method = CapturingRules::class.java.getDeclaredMethod("capturing\$lambda\$0", Int::class.java)
            assertThat(method.declaredParameterNames()).isEqualTo(listOf("\$start"))
        }

        /** A `$DefaultImpls` method takes the interface instance before the declared parameters. */
        @Test
        fun testKotlinNamesThatDontLineUpWithTheJvmParametersFallBackToTheCompiledNames() {
            val method = Class.forName("${WithDefault::class.java.name}\$DefaultImpls")
                .getMethod("rule", WithDefault::class.java, Int::class.java)
            assertThat(method.declaredParameterNames()).isEqualTo(listOf("\$this", "start"))
        }

        @Test
        fun testAClassWithoutAClassFileHasNoNames() {
            assertThat(sharedOrdered(withClassFile(JavaConsumerFilters::class.java) { null }).declaredParameterNames())
                .isNull()
        }

        @Test
        fun testAClassFileThatEndsEarlyHasNoNames() {
            val truncated = withClassFile(JavaConsumerFilters::class.java) { it.copyOf(it.size / 2) }
            assertThat(sharedOrdered(truncated).declaredParameterNames()).isNull()
        }
    }

    /** Each source is read with the class file out of reach, so only that source can supply the names. */
    @Nested
    inner class EachSourceOfNames {

        @Test
        fun testKotlinNamesComeFromItsMetadata() {
            val rules = withClassFile(KotlinConsumerRules::class.java) { null }
            assertThat(rules.getMethod("ordered", Int::class.java, Int::class.java).declaredParameterNames())
                .isEqualTo(listOf("end", "start"))
        }

        @Test
        fun testJavaNamesComeFromParametersWhenCompiledWithIt() {
            val filters = withClassFile(JavaFilterFixture::class.java) { null }
            assertThat(filters.getMethod("price_filter_odd", Int::class.java).declaredParameterNames())
                .isEqualTo(listOf("start"))
        }

        @Test
        fun testJavaNamesComeFromTheClassFileOtherwise() {
            assertThat(sharedOrdered(JavaConsumerFilters::class.java).declaredParameterNames())
                .isEqualTo(listOf("start", "end"))
        }

        /** Kotlin writes `value` into the bridge's debug information too, which would let both bind. */
        @Test
        fun testABridgeForAGenericOverrideHasNoNames() {
            val methods = GenericOverride::class.java.declaredMethods.filter { it.name == "check" }
            assertThat(methods.single { it.isBridge }.declaredParameterNames()).isNull()
            assertThat(methods.single { !it.isBridge }.declaredParameterNames()).isEqualTo(listOf("value"))
        }

        /** `@JvmOverloads` forms have no Kotlin declaration of their own, but their class file has their names. */
        @Test
        fun testAJvmOverloadsFormHasTheNamesOfTheParametersItTakes() {
            val shorter = KotlinConsumerRules::class.java.getMethod("upTo", Int::class.java)
            assertThat(shorter.declaredParameterNames()).isEqualTo(listOf("start"))
        }

        /** The class file names each method by its descriptor too, which keeps overloads apart. */
        @Test
        fun testEachOverloadHasItsOwnNames() {
            val filters = JavaConsumerFilters::class.java
            assertThat(filters.getMethod("overloaded", Int::class.java).declaredParameterNames())
                .isEqualTo(listOf("start"))
            assertThat(filters.getMethod("overloaded", Long::class.java, Int::class.java).declaredParameterNames())
                .isEqualTo(listOf("big", "small"))
        }
    }

    private fun sharedOrdered(filters: Class<*>) = filters.getMethod("sharedOrdered", Int::class.java, Int::class.java)

    /** Loads [type] again, in a loader that serves what [classFile] makes of its class file in place of it. */
    private fun withClassFile(type: Class<*>, classFile: (ByteArray) -> ByteArray?): Class<*> {
        val path = type.name.replace('.', '/') + ".class"
        val bytes = type.classLoader.getResourceAsStream(path)!!.use { it.readBytes() }
        val loader = object : ClassLoader(type.classLoader) {
            override fun loadClass(name: String, resolve: Boolean): Class<*> = synchronized(getClassLoadingLock(name)) {
                if (name == type.name) {
                    findLoadedClass(name) ?: defineClass(name, bytes, 0, bytes.size)
                } else {
                    super.loadClass(name, resolve)
                }
            }

            override fun getResourceAsStream(name: String): InputStream? =
                if (name == path) classFile(bytes)?.inputStream() else super.getResourceAsStream(name)
        }
        return loader.loadClass(type.name)
    }

    interface WithDefault {
        fun rule(start: Int): Boolean = start > 1
    }

    interface Check<T> {
        fun check(value: T): Boolean
    }

    object GenericOverride : Check<Int> {
        override fun check(value: Int): Boolean = value > 1
    }

    object CapturingRules {
        fun capturing(start: Int): () -> Boolean = { start > 1 }
    }

    class StrippedFilterFixture : JavaStrippedFilters() {
        @GeneratedParametersTest(name = "{arguments}", filters = ["strippedOrdered"])
        fun ordered(@IntRangeSource(min = 1, max = 3) start: Int, @IntRangeSource(min = 1, max = 3) end: Int) {
        }
    }

    private fun positionalWarning(filter: String): String =
        "Filter [$filter] binds to the test's parameters by position, because the class files don't " +
            "record their names. Compile with -parameters to bind by name."

    private fun warningsWhile(run: () -> Unit): List<String> {
        val logger = Logger.getLogger(ParameterFilter::class.java.name)
        val warnings = mutableListOf<String>()
        val handler = object : Handler() {
            override fun publish(record: LogRecord) {
                if (record.level == Level.WARNING) warnings += record.message
            }

            override fun flush() {}

            override fun close() {}
        }
        logger.addHandler(handler)
        try {
            run()
        } finally {
            logger.removeHandler(handler)
        }
        return warnings
    }
}
