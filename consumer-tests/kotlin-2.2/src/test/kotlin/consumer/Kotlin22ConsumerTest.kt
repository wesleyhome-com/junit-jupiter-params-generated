package consumer

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.ValuesFrom
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@JvmInline
value class Cents(val value: Long)

class Kotlin22ConsumerTest {

    @GeneratedParametersTest(name = "{arguments}")
    fun order(@IntRangeSource(min = 1, max = 3) quantity: Int, @ValuesFrom("prices") price: Cents) {
        orders += "$quantity x ${price.value}"
    }

    @Test
    fun runsOnSpringBoot40Versions() {
        assertEquals("2.2.21", KotlinVersion.CURRENT.toString())
        assertEquals("6.0.3", Test::class.java.`package`.implementationVersion)
    }

    companion object {
        private val orders = mutableListOf<String>()

        @JvmStatic
        fun prices(): List<Cents> = listOf(Cents(100), Cents(250))

        @JvmStatic
        fun order_filter(quantity: Int, price: Cents): Boolean = quantity * price.value <= 500

        /** Filters no test, so the annotation processor warns about it: the sign that KSP ran it. */
        @JvmStatic
        fun renamedOrder_filter(quantity: Int): Boolean = true

        @AfterAll
        @JvmStatic
        fun everyAcceptedOrderRan() {
            assertEquals(listOf("1 x 100", "1 x 250", "2 x 100", "2 x 250", "3 x 100"), orders.sorted())
        }
    }
}
