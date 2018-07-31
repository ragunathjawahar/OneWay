package io.mobsgeeks.oneway.catalogue.bmi

import io.mobsgeeks.oneway.catalogue.bmi.BmiCategory.SEVERELY_UNDERWEIGHT
import io.mobsgeeks.oneway.catalogue.bmi.BmiCategory.VERY_SEVERELY_UNDERWEIGHT

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
      else -> throw IllegalStateException()
    }
}
