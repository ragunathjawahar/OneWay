package io.redgreen.oneway.catalogue.bmi

import io.redgreen.oneway.catalogue.bmi.calculator.BmiCategory
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem

interface BmiView {
  fun showBmi(bmi: Double)
  fun showCategory(category: BmiCategory)
  fun showWeight(weight: Double, measurementSystem: MeasurementSystem)
  fun showHeight(height: Double, measurementSystem: MeasurementSystem)
  fun showMeasurementSystem(measurementSystem: MeasurementSystem)
}
