package io.redgreen.oneway

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CanaryUnitTest {
  @Test fun `test environment is setup`() {
    val sum = 2 + 2
    assertThat(sum)
        .isEqualTo(4)
  }
}
