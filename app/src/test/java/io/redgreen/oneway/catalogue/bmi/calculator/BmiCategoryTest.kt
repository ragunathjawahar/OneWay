package io.redgreen.oneway.catalogue.bmi.calculator

import com.google.common.truth.Truth.assertThat
import io.redgreen.oneway.catalogue.bmi.calculator.BmiCategory.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class BmiCategoryTest {
  @TestFactory fun testCategories(): Iterable<DynamicTest> {
    val weightHeightCategories = mapOf(
        (30 to 220) to VERY_SEVERELY_UNDERWEIGHT,
        (30 to 140) to SEVERELY_UNDERWEIGHT,
        (30 to 136) to UNDERWEIGHT,
        (30 to 126) to NORMAL,
        (37 to 120) to OVERWEIGHT,
        (44 to 120) to OBESE_CLASS_1,
        (51 to 120) to OBESE_CLASS_2,
        (59 to 120) to OBESE_CLASS_3,
        (65 to 120) to OBESE_CLASS_4,
        (73 to 120) to OBESE_CLASS_5,
        (89 to 120) to OBESE_CLASS_6
    )

    return weightHeightCategories.entries
        .map { (weightHeight, category) ->
          val weight = weightHeight.first
          val height = weightHeight.second
          val displayName = "when weight is ${weight}kg and height is ${height}cm, then category is $category"

          DynamicTest.dynamicTest(displayName) {
            val bmiResult = BmiCalculator.calculate(weight.toDouble(), height.toDouble())
            assertThat(bmiResult.category)
                .isEqualTo(category)
          }
        }
        .toList()
  }
}
