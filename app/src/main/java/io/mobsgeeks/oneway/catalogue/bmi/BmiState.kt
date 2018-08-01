package io.mobsgeeks.oneway.catalogue.bmi

import io.mobsgeeks.oneway.catalogue.bmi.MeasurementSystem.IMPERIAL

data class BmiState(
    private val weightInKg: Double,
    private val heightInCm: Double,
    val measurementSystem: MeasurementSystem
) {
  val weight: Double
    get() = if (measurementSystem == IMPERIAL)
      toPounds(weightInKg)
    else
      weightInKg

  val height: Double
    get() = if (measurementSystem == IMPERIAL)
      toFoot(heightInCm)
    else
      heightInCm

  val bmi: Double
    get() = bmiResult.bmi

  val category: BmiCategory
    get() = bmiResult.category

  private val bmiResult by lazy {
    BmiCalculator.calculate(weightInKg, heightInCm)
  }

  private fun toPounds(weightInKg: Double): Double =
      weightInKg * 2.2046226218

  private fun toFoot(heightInCm: Double): Double =
      heightInCm / 30.48
}
