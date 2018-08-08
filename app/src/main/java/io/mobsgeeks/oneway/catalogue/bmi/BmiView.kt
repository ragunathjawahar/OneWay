package io.mobsgeeks.oneway.catalogue.bmi

import io.mobsgeeks.oneway.catalogue.bmi.domain.BmiCategory
import io.mobsgeeks.oneway.catalogue.bmi.domain.MeasurementSystem

interface BmiView {
  fun showBmi(bmi: Double)
  fun showCategory(category: BmiCategory)
  fun showWeight(weight: Double, measurementSystem: MeasurementSystem)
  fun showHeight(height: Double, measurementSystem: MeasurementSystem)
  fun showMeasurementSystem(measurementSystem: MeasurementSystem)
}
