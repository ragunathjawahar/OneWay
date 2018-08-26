package io.redgreen.oneway.catalogue.bmi

import android.os.Parcelable
import io.redgreen.oneway.catalogue.bmi.calculator.BmiCalculator
import io.redgreen.oneway.catalogue.bmi.calculator.BmiCategory
import io.redgreen.oneway.catalogue.bmi.calculator.ImperialUnitConverter
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem.IMPERIAL
import kotlinx.android.parcel.Parcelize

@Parcelize data class BmiState(
    private val weightInKg: Double,
    private val heightInCm: Double,
    val measurementSystem: MeasurementSystem
) : Parcelable {
  val weight: Double
    get() = if (measurementSystem == IMPERIAL)
      ImperialUnitConverter.toPounds(weightInKg)
    else
      weightInKg

  val height: Double
    get() = if (measurementSystem == IMPERIAL)
      ImperialUnitConverter.toFoot(heightInCm)
    else
      heightInCm

  val bmi: Double
    get() = bmiResult.bmi

  val category: BmiCategory
    get() = bmiResult.category

  private val bmiResult
    get() = BmiCalculator.calculate(weightInKg, heightInCm)

  fun updateWeight(weightInKg: Double): BmiState =
      copy(weightInKg = weightInKg)

  fun updateHeight(heightInCm: Double): BmiState =
      copy(heightInCm = heightInCm)

  fun updateMeasurementSystem(measurementSystem: MeasurementSystem) =
      copy(measurementSystem = measurementSystem)
}
