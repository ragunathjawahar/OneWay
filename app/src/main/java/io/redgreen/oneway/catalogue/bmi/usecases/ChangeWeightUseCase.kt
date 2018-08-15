package io.redgreen.oneway.catalogue.bmi.usecases

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom
import io.redgreen.oneway.catalogue.bmi.BmiState
import io.redgreen.oneway.catalogue.bmi.ChangeWeightIntention

class ChangeWeightUseCase(
    val timeline: Observable<BmiState>
) : ObservableTransformer<ChangeWeightIntention, BmiState> {
  override fun apply(changeWeightIntentions: Observable<ChangeWeightIntention>): ObservableSource<BmiState> {
    return changeWeightIntentions
        .withLatestFrom(timeline) { changeWeightIntention: ChangeWeightIntention, state ->
          state.updateWeight(changeWeightIntention.weightInKg)
        }
  }
}
