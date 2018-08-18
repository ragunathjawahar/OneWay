package io.redgreen.oneway.catalogue.bmi.calculator

import com.google.common.collect.Range
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class BmiCalculatorTest {
  @Test fun `when weight is 0, then BMI is 0`() {
    // when
    val result = BmiCalculator.calculate(0.0, 170.0)

    // then
    assertThat(result.bmi)
        .isEqualTo(0.0)
  }

  @Test fun `when height is 0, then BMI is 0`() {
    // when
    val bmiResult = BmiCalculator.calculate(50.0, 0.0)

    // then
    assertThat(bmiResult.bmi)
        .isEqualTo(0.0)
  }

  @Test fun `when height and weight are 0, then BMI is 0`() {
    // when
    val bmiResult = BmiCalculator.calculate(0.0, 0.0)

    // then
    assertThat(bmiResult.bmi)
        .isEqualTo(0.0)
  }

  @Test fun `when weight is negative, then throw exception`() {
    Assertions.assertThrows(IllegalStateException::class.java) {
      BmiCalculator.calculate(-1.0, 170.0)
    }
  }

  @Test fun `when height is negative, then throw exception`() {
    Assertions.assertThrows(IllegalStateException::class.java) {
      BmiCalculator.calculate(50.0, -150.0)
    }
  }

  @TestFactory fun testBmis(): Iterable<DynamicTest> {
    val weightHeightBmis = mapOf(
        (30 to 220) to 6.1,
        (30 to 140) to 15.3,
        (30 to 136) to 16.2,
        (30 to 126) to 18.8,
        (37 to 120) to 25.6,
        (44 to 120) to 30.5,
        (51 to 120) to 35.4,
        (59 to 120) to 40.9,
        (65 to 120) to 45.1,
        (73 to 120) to 50.6,
        (89 to 120) to 61.8
    )

    return weightHeightBmis.entries
        .map { (weightHeight, bmi) ->
          val weight = weightHeight.first
          val height = weightHeight.second
          val bmiSinglePrecision = String.format("%.01f", bmi)
          val displayName = "when weight is ${weight}kg and height is ${height}cm, then BMI is ~$bmiSinglePrecision"

          DynamicTest.dynamicTest(displayName) {
            val bmiResult = BmiCalculator.calculate(weight.toDouble(), height.toDouble())
            assertThat(bmiResult.bmi)
                .isIn(Range.closed(bmi - 0.1, bmi + 0.1))
          }
        }
        .toList()
  }
}
