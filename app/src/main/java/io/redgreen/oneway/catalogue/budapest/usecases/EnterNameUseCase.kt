package io.redgreen.oneway.catalogue.budapest.usecases

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom
import io.redgreen.oneway.catalogue.budapest.BudapestState
import io.redgreen.oneway.catalogue.budapest.EnterNameIntention
import io.redgreen.oneway.catalogue.budapest.GreeterState
import io.redgreen.oneway.catalogue.budapest.StrangerState

class EnterNameUseCase(
    private val sourceCopy: Observable<BudapestState>
) : ObservableTransformer<EnterNameIntention, BudapestState> {
  override fun apply(
      enterNameIntentions: Observable<EnterNameIntention>
  ): ObservableSource<BudapestState> {
    return enterNameIntentions
        .withLatestFrom(sourceCopy, ::reduceStateWithIntention)
  }

  private fun reduceStateWithIntention(
      intention: EnterNameIntention,
      state: BudapestState
  ): BudapestState {
    val hasName = intention.name.isNotBlank()

    return when (state) {
      is StrangerState -> {
        if (hasName) {
          state.onEnterName(intention)
        } else {
          state.blankName()
        }
      }

      is GreeterState -> {
        if (hasName) {
          state.onEnterName(intention)
        } else {
          state.blankName()
        }
      }
    }
  }
}
