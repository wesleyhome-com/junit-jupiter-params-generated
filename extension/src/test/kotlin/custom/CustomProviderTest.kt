package custom

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.AbstractParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.testkit.invocationNames
import org.junit.jupiter.api.Test

/**
 * Stands in for a consumer of the published artifacts: everything here is written against the
 * public API only, and each provider sits behind a base class of its own rather than extending the
 * library's directly.
 */
class CustomProviderTest {

    @Test
    fun testProviderBehindAGenericIntermediate() {
        assertThat(invocationNames(Fixture::class.java, "annotatedCustomSource"))
            .isEqualTo(listOf("0", "2", "4", "6"))
    }

    @Test
    fun testProviderBehindAnIntermediateThatFixesTheType() {
        assertThat(invocationNames(Fixture::class.java, "typeOnlyCustomSource"))
            .isEqualTo(listOf("hei", "hola"))
    }

    class Fixture {

        @GeneratedParametersTest(name = "{arguments}")
        fun annotatedCustomSource(@EvenSource(max = 6) value: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun typeOnlyCustomSource(@GreetingSource value: String) {
        }
    }
}

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(EvenSourceDataProvider::class)
annotation class EvenSource(val max: Int)

/** An intermediate that passes its own type parameter through to the library base. */
abstract class SteppedIntProvider<A : Annotation> : AbstractAnnotatedParameterDataProvider<Int, A>() {

    protected abstract fun progression(annotation: A): IntProgression

    final override fun createParameterOptionsData(testParameter: TestParameter): List<Int> =
        progression(findAnnotation(testParameter)!!).toList()
}

class EvenSourceDataProvider : SteppedIntProvider<EvenSource>() {
    override fun progression(annotation: EvenSource): IntProgression = 0..annotation.max step 2
}

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(GreetingSourceDataProvider::class)
annotation class GreetingSource

/** An intermediate that binds the type argument itself, leaving nothing parameterized below it. */
abstract class StringSourceProvider : AbstractParameterDataProvider<String>()

class GreetingSourceDataProvider : StringSourceProvider() {
    override fun createParameterOptionsData(testParameter: TestParameter): List<String> =
        listOf("hei", "hola")
}
