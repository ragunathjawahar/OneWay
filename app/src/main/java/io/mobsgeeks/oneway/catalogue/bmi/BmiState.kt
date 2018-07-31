package io.mobsgeeks.oneway.catalogue.bmi

import io.mobsgeeks.oneway.catalogue.bmi.BmiCategory.*

data class BmiState(
    private val weightInKg: Double,
    private val heightInCm: Double
) {
  private val weight: Double = if (weightInKg >= 0)
    weightInKg
  else
    throw IllegalStateException("'weightInKg' cannot be negative.")

  private val height: Double = if (heightInCm >= 0)
    heightInCm / 100.0
  else
    throw IllegalStateException("'heightInCm' cannot be negative.")

  val bmi: Double
    get() = if (height != 0.0) weight / (height * height) else 0.0

  val category: BmiCategory
    get() = when(bmi) {
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
