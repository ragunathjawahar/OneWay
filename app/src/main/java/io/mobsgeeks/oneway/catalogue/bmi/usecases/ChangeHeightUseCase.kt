package io.mobsgeeks.oneway.catalogue.bmi.usecases

import io.mobsgeeks.oneway.catalogue.bmi.BmiState
import io.mobsgeeks.oneway.catalogue.bmi.ChangeHeightIntention
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom

class ChangeHeightUseCase(
    private val timeline: Observable<BmiState>
) : ObservableTransformer<ChangeHeightIntention, BmiState> {
  override fun apply(changeHeightIntentions: Observable<ChangeHeightIntention>): ObservableSource<BmiState> {
    return changeHeightIntentions
        .withLatestFrom(timeline) { changeHeightIntention, bmiState ->
          bmiState.updateHeight(changeHeightIntention.heightInCm)
        }
  }
}
