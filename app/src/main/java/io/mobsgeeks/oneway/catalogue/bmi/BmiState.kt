package io.mobsgeeks.oneway.catalogue.bmi

import android.os.Parcelable
import io.mobsgeeks.oneway.catalogue.bmi.MeasurementSystem.IMPERIAL
import io.mobsgeeks.oneway.catalogue.bmi.MeasurementSystem.SI
import kotlinx.android.parcel.Parcelize

@Parcelize data class BmiState(
    private val weightInKg: Double,
    private val heightInCm: Double,
    val measurementSystem: MeasurementSystem
) : Parcelable {
  companion object {
    val INITIAL = BmiState(48.0, 160.0, SI)
  }

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

  fun updateWeight(weightInKg: Double): BmiState =
      copy(weightInKg = weightInKg)

  fun updateHeight(heightInCm: Double): BmiState =
      copy(heightInCm = heightInCm)

  fun updateMeasurementSystem(measurementSystem: MeasurementSystem) =
      copy(measurementSystem = measurementSystem)

  private fun toPounds(weightInKg: Double): Double =
      weightInKg * 2.2046226218

  private fun toFoot(heightInCm: Double): Double =
      heightInCm / 30.48
}
