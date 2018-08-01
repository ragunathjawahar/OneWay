package io.mobsgeeks.oneway.catalogue.bmi

import io.mobsgeeks.oneway.catalogue.bmi.usecases.BmiUseCases
import io.reactivex.Observable

object BmiModel {
  fun bind(
      intentions: Observable<BmiIntention>,
      useCases: BmiUseCases
  ): Observable<BmiState> {
    return Observable.mergeArray(
        intentions
            .ofType(ChangeWeightIntention::class.java)
            .compose(useCases.changeWeightUseCase),
        intentions
            .ofType(ChangeHeightIntention::class.java)
            .compose(useCases.changeHeightUseCase),
        intentions
            .ofType(ChangeMeasurementSystemIntention::class.java)
            .compose(useCases.changeMeasurementSystemUseCase)
    )
  }
}
