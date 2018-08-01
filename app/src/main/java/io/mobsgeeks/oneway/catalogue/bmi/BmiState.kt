package io.mobsgeeks.oneway.catalogue.bmi

data class BmiState(
    private val weightInKg: Double,
    private val heightInCm: Double
) {
  private val bmiResult by lazy { BmiCalculator.calculate(weightInKg, heightInCm) }

  val bmi: Double
    get() = bmiResult.bmi

  val category: BmiCategory
    get() = bmiResult.category
}
