package io.redgreen.oneway.catalogue.bmi

import io.reactivex.Observable
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.catalogue.bmi.usecases.BmiUseCases

object BmiModel {
  fun createSource(
      intentions: Observable<BmiIntention>,
      sourceLifecycleEvents: Observable<SourceLifecycleEvent>,
      useCases: BmiUseCases
  ): Observable<BmiState> {
    val changeWeightIntentions = intentions
        .ofType(ChangeWeightIntention::class.java)
    val changeHeightIntentions = intentions
        .ofType(ChangeHeightIntention::class.java)
    val changeMeasurementIntentions = intentions
        .ofType(ChangeMeasurementSystemIntention::class.java)

    return Observable.mergeArray(
        sourceLifecycleEvents.compose(useCases.sourceCreatedUseCase),
        sourceLifecycleEvents.compose(useCases.sourceRestoredUseCase),
        changeWeightIntentions.compose(useCases.changeWeightUseCase),
        changeHeightIntentions.compose(useCases.changeHeightUseCase),
        changeMeasurementIntentions.compose(useCases.changeMeasurementSystemUseCase)
    )
  }
}
