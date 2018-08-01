package io.mobsgeeks.oneway.catalogue.bmi

import io.reactivex.Observable

class BmiIntentions(
    private val weightChanges: Observable<Int>,
    private val heightChanges: Observable<Int>,
    private val measurementSystemCheckedChanges: Observable<Boolean>
) {
  fun stream(): Observable<BmiIntention> {
    return Observable.merge(
        changeWeight().share(),
        changeHeight().share(),
        changeMeasurementSystem().share()
    )
  }

  private fun changeWeight(): Observable<ChangeWeightIntention> =
      weightChanges.map { ChangeWeightIntention(it.toDouble()) }

  private fun changeHeight(): Observable<ChangeHeightIntention> =
      heightChanges.map { ChangeHeightIntention(it.toDouble()) }

  private fun changeMeasurementSystem(): Observable<ChangeMeasurementSystemIntention> =
      measurementSystemCheckedChanges
          .map { if (it) MeasurementSystem.IMPERIAL else MeasurementSystem.SI }
          .map { ChangeMeasurementSystemIntention(it) }
}
