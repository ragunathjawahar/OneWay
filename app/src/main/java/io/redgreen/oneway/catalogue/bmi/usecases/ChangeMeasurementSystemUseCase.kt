package io.redgreen.oneway.catalogue.bmi.usecases

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom
import io.redgreen.oneway.catalogue.bmi.BmiState
import io.redgreen.oneway.catalogue.bmi.ChangeMeasurementSystemIntention

class ChangeMeasurementSystemUseCase(
    private val sourceCopy: Observable<BmiState>
) : ObservableTransformer<ChangeMeasurementSystemIntention, BmiState> {
  override fun apply(
      changeMeasurementSystemIntentions: Observable<ChangeMeasurementSystemIntention>
  ): ObservableSource<BmiState> {
    return changeMeasurementSystemIntentions
        .withLatestFrom(sourceCopy) { changeMeasurementSystemIntention, bmiState ->
          bmiState.updateMeasurementSystem(changeMeasurementSystemIntention.measurementSystem)
        }
  }
}
