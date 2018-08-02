package io.mobsgeeks.oneway.catalogue.bmi

import io.mobsgeeks.oneway.catalogue.bmi.MeasurementSystem.IMPERIAL
import io.mobsgeeks.oneway.catalogue.bmi.MeasurementSystem.SI
import io.reactivex.Observable

class BmiIntentions(
    private val weightChanges: Observable<Int>,
    private val heightChanges: Observable<Int>,
    private val measurementSystemCheckedChanges: Observable<Boolean>,
    private val bmiOffset: BmiOffset
) {
  fun stream(): Observable<BmiIntention> {
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
