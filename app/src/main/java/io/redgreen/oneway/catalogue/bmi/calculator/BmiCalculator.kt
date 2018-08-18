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
    in 15.0 .. 16.0 -> SEVERELY_UNDERWEIGHT
    in 16.0 .. 18.5 -> UNDERWEIGHT
    in 18.5 .. 25.0 -> NORMAL
    in 25.0 .. 30.0 -> OVERWEIGHT
    in 30.0 .. 35.0 -> OBESE_CLASS_1
    in 35.0 .. 40.0 -> OBESE_CLASS_2
    in 40.0 .. 45.0 -> OBESE_CLASS_3
    in 45.0 .. 50.0 -> OBESE_CLASS_4
    in 50.0 .. 60.0 -> OBESE_CLASS_5
    in 60.0 .. Double.MAX_VALUE -> OBESE_CLASS_6
    else -> throw IllegalStateException("BMI is $bmi. This should never happen.")
  }
}

data class BmiResult(
    val bmi: Double,
    val category: BmiCategory
)
