package io.mobsgeeks.oneway.catalogue.bmi

import com.google.common.truth.Truth.assertThat
import io.mobsgeeks.oneway.catalogue.bmi.BmiCategory.SEVERELY_UNDERWEIGHT
import io.mobsgeeks.oneway.catalogue.bmi.BmiCategory.VERY_SEVERELY_UNDERWEIGHT
import org.junit.Test

class BmiCategoryTest {
  @Test fun `BMI 0 is very severely underweight`() {
    // when
    val bmiState = BmiState(0.0, 90.0)

    // then
    assertThat(bmiState.category)
        .isEqualTo(VERY_SEVERELY_UNDERWEIGHT)
  }

  @Test fun `BMI 15 is very severely underweight`() {
    // when
    val bmiState = BmiState(49.0, 181.0)

    // then
    assertThat(bmiState.category)
        .isEqualTo(VERY_SEVERELY_UNDERWEIGHT)
  }

  @Test fun `BMI 15dot1 is severely underweight`() {
    // when
    val bmiState = BmiState(49.0, 180.0)

    // then
    assertThat(bmiState.category)
        .isEqualTo(SEVERELY_UNDERWEIGHT)
  }
}
