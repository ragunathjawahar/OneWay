package io.mobsgeeks.oneway.android

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CanaryInstrumentedTest {
  @Test fun useAppContext() {
    val appContext = InstrumentationRegistry.getTargetContext()
    assertEquals("io.mobsgeeks.oneway.android.test", appContext.packageName)
  }
}
