package io.redgreen.oneway.catalogue.bmi.calculator

import io.redgreen.oneway.catalogue.bmi.calculator.BmiCategory.*

object BmiCalculator {
  fun calculate(weightInKg: Double, heightInCm: Double): BmiResult {
    if (weightInKg < 0)
      throw IllegalStateException("'weightInKg' cannot be negative.")

    if (heightInCm < 0)
      throw IllegalStateException("'heightInCm' cannot be negative.")

    val heightInM = heightInCm / 100
    val bmi = if (heightInCm != 0.0) weightInKg / (heightInM * heightInM) else 0.0
    val category = category(bmi)
    return BmiResult(bmi, category)
  }

  private fun category(bmi: Double): BmiCategory = when(bmi) {
    in 0.0  .. 15.0 -> VERY_SEVERELY_UNDERWEIGHT
    in 15.1 .. 16.0 -> SEVERELY_UNDERWEIGHT
    in 16.1 .. 18.5 -> UNDERWEIGHT
    in 18.6 .. 25.0 -> NORMAL
    in 25.1 .. 30.0 -> OVERWEIGHT
    in 30.1 .. 35.0 -> OBESE_CLASS_1
    in 35.1 .. 40.0 -> OBESE_CLASS_2
    in 40.1 .. 45.0 -> OBESE_CLASS_3
    in 45.1 .. 50.0 -> OBESE_CLASS_4
    in 50.1 .. 60.0 -> OBESE_CLASS_5
    else -> OBESE_CLASS_6
  }
}

data class BmiResult(
    val bmi: Double,
    val category: BmiCategory
)
