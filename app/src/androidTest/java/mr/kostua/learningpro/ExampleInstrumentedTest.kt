package mr.kostua.learningpro

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented alert_circle, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under alert_circle.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("mr.kotlin.learningpro", appContext.packageName)
    }
}
