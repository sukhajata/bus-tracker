package bustracker.sukhajata.com.bustracker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest {
    val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun mainActivity_onCreate_notNull() {
        val mainActivity : MainActivity = Robolectric.setupActivity(MainActivity::class.java)
        assert(mainActivity != null)
        assert(mainActivity.getClient() != null)
    }

}
