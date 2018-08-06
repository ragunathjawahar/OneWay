package io.mobsgeeks.oneway.catalogue.bmi

import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.catalogue.bmi.usecases.BmiUseCases
import io.reactivex.Observable

object BmiModel {
  fun createSource(
      intentions: Observable<BmiIntention>,
      sourceEvents: Observable<SourceEvent>,
      useCases: BmiUseCases
  ): Observable<BmiState> {
    val changeWeightIntentions = intentions
        .ofType(ChangeWeightIntention::class.java)
    val changeHeightIntentions = intentions
        .ofType(ChangeHeightIntention::class.java)
    val changeMeasurementIntentions = intentions
        .ofType(ChangeMeasurementSystemIntention::class.java)

    return Observable.mergeArray(
        sourceEvents.compose(useCases.sourceCreatedUseCase),
        sourceEvents.compose(useCases.sourceRestoredUseCase),
        changeWeightIntentions.compose(useCases.changeWeightUseCase),
        changeHeightIntentions.compose(useCases.changeHeightUseCase),
        changeMeasurementIntentions.compose(useCases.changeMeasurementSystemUseCase)
    )
  }
}
