package io.mobsgeeks.oneway.catalogue.bmi

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BmiStateTest {
  @Test fun `it grants access to bmi and category`() {
    // given
    val weightInKg = 80.0
    val heightInCm = 180.0

    // when
    val bmiState = BmiState(weightInKg, heightInCm)

    // then
    val calculatedBmiResult = BmiCalculator.calculate(weightInKg, heightInCm)
    assertThat(bmiState.bmi)
        .isEqualTo(calculatedBmiResult.bmi)

    assertThat(bmiState.category)
        .isEqualTo(calculatedBmiResult.category)
  }
}
