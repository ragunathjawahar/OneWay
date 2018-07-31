package io.mobsgeeks.oneway.catalogue.bmi

import com.google.common.truth.Truth.assertThat
import io.mobsgeeks.oneway.catalogue.bmi.BmiCategory.*
import org.junit.Test

/** BMI values for tests were derived from - [Google's BMI Calculator](https://www.google.co.in/search?q=bmi+calculator). */
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

  @Test fun `BMI 16 is severely underweight`() {
    // when
    val bmiState = BmiState(51.7, 180.0)

    // then
    assertThat(bmiState.category)
        .isEqualTo(SEVERELY_UNDERWEIGHT)
  }

  @Test fun `BMI 60 and above is obese class VI`() {
    // when
    val bmiState = BmiState(300.0, 180.0)

    // then
    assertThat(bmiState.category)
        .isEqualTo(OBESE_CLASS_6)
  }
}
