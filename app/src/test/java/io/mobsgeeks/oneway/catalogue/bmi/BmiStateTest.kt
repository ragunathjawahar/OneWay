package io.mobsgeeks.oneway.catalogue.bmi

import com.google.common.collect.Range
import com.google.common.truth.Truth.assertThat
import io.mobsgeeks.oneway.catalogue.bmi.calculator.BmiCalculator
import io.mobsgeeks.oneway.catalogue.bmi.calculator.MeasurementSystem.IMPERIAL
import io.mobsgeeks.oneway.catalogue.bmi.calculator.MeasurementSystem.SI
import org.junit.Test

class BmiStateTest {
  @Test fun `it grants access to bmi and category`() {
    // given
    val weightInKg = 80.0
    val heightInCm = 180.0

    // when
    val bmiState = BmiState(weightInKg, heightInCm, SI)

    // then
    val calculatedBmiResult = BmiCalculator.calculate(weightInKg, heightInCm)
    assertThat(bmiState.bmi)
        .isEqualTo(calculatedBmiResult.bmi)

    assertThat(bmiState.category)
        .isEqualTo(calculatedBmiResult.category)
  }

  @Test fun `it can provide weight and height in metric system`() {
    // when
    val weightInKg = 100.0
    val heightInCm = 180.0
    val bmiState = BmiState(weightInKg, heightInCm, SI)

    // then
    assertThat(bmiState.weight)
        .isEqualTo(weightInKg)

    assertThat(bmiState.height)
        .isEqualTo(heightInCm)
  }

  @Test fun `it can provide weight and height in imperial system`() {
    // given
    val weightInKg = 100.0
    val heightInCm = 180.0

    // when
    val bmiState = BmiState(weightInKg, heightInCm, IMPERIAL)

    // then
    assertThat(bmiState.weight)
        .isIn(Range.closed(220.4, 220.5))

    assertThat(bmiState.height)
        .isIn(Range.closed(5.9, 6.0))
  }
}
