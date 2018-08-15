package io.redgreen.oneway.catalogue.bmi.usecases

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom
import io.redgreen.oneway.catalogue.bmi.BmiState
import io.redgreen.oneway.catalogue.bmi.ChangeHeightIntention

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
