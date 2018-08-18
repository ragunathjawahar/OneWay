package io.redgreen.oneway.catalogue.bmi

import com.google.common.collect.Range
import com.google.common.truth.Truth.assertThat
import io.redgreen.oneway.catalogue.bmi.calculator.BmiCalculator
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem.IMPERIAL
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem.SI
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("BMI state")
class BmiStateTest {
  private val weightInKg = 80.0
  private val heightInCm = 180.0

  @DisplayName("in metric system")
  @Nested inner class InMetricSystem {
    private val bmiState = BmiState(weightInKg, heightInCm, SI)

    @Test fun `provides weight and height in kg and cm`() {
      assertThat(bmiState.weight)
          .isEqualTo(weightInKg)

      assertThat(bmiState.height)
          .isEqualTo(heightInCm)
    }

    @Test fun `calculates BMI and category`() {
      val calculatedBmiResult = BmiCalculator.calculate(weightInKg, heightInCm)

      assertThat(bmiState.bmi)
          .isEqualTo(calculatedBmiResult.bmi)
      assertThat(bmiState.category)
          .isEqualTo(calculatedBmiResult.category)
    }
  }

  @DisplayName("in imperial system")
  @Nested inner class InImperialSystem {
    private val bmiState = BmiState(weightInKg, heightInCm, IMPERIAL)

    @Test fun `provides weight and height in lb and ft`() {
      assertThat(bmiState.weight)
          .isIn(Range.closed(176.3, 176.4))

      assertThat(bmiState.height)
          .isIn(Range.closed(5.9, 6.0))
    }

    @Test fun `calculates BMI and category`() {
      val calculatedBmiResult = BmiCalculator.calculate(weightInKg, heightInCm)

      assertThat(bmiState.bmi)
          .isEqualTo(calculatedBmiResult.bmi)
      assertThat(bmiState.category)
          .isEqualTo(calculatedBmiResult.category)
    }
  }
}
