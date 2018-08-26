package io.redgreen.oneway.catalogue.bmi.calculator

object ImperialUnitConverter {
  fun toPounds(weightInKg: Double): Double =
      weightInKg * 2.2046226218

  fun toFoot(heightInCm: Double): Double =
      heightInCm / 30.48
}
