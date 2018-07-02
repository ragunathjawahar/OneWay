package io.mobsgeeks.oneway

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class) class CanaryInstrumentedTest {
  @Test fun useAppContext() {
    val appContext = InstrumentationRegistry.getTargetContext()
    assertThat(appContext)
        .isNotNull()
  }
}
