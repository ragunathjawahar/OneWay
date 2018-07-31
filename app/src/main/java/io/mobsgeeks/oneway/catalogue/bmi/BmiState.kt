package io.mobsgeeks.oneway.catalogue.bmi

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
}
