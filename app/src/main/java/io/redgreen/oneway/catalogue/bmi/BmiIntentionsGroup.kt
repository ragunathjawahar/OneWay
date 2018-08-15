package io.redgreen.oneway.catalogue.bmi

import io.reactivex.Observable
import io.redgreen.oneway.IntentionsGroup
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem.IMPERIAL
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem.SI

class BmiIntentionsGroup(
    private val weightChanges: Observable<Int>,
    private val heightChanges: Observable<Int>,
    private val measurementSystemCheckedChanges: Observable<Boolean>,
    private val bmiOffset: BmiOffset
) : IntentionsGroup<BmiIntention> {
  override fun intentions(): Observable<BmiIntention> {
    return Observable.merge(
        changeWeight().share(),
        changeHeight().share(),
        changeMeasurementSystem().share()
    )
  }

  private fun changeWeight(): Observable<ChangeWeightIntention> =
      weightChanges
          .map { it + bmiOffset.minWeightInKg }
          .map { ChangeWeightIntention(it) }

  private fun changeHeight(): Observable<ChangeHeightIntention> =
      heightChanges
          .map { it + bmiOffset.minHeightInCm }
          .map { ChangeHeightIntention(it) }

  private fun changeMeasurementSystem(): Observable<ChangeMeasurementSystemIntention> =
      measurementSystemCheckedChanges
          .map { checked -> if (checked) IMPERIAL else SI }
          .map { ChangeMeasurementSystemIntention(it) }
}

data class BmiOffset(
    val minWeightInKg: Double,
    val minHeightInCm: Double
)
