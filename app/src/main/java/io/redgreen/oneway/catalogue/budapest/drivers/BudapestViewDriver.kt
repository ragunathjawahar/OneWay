package io.redgreen.oneway.catalogue.budapest.drivers

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.redgreen.oneway.catalogue.budapest.BudapestState
import io.redgreen.oneway.catalogue.budapest.BudapestView
import io.redgreen.oneway.drivers.ViewDriver

class BudapestViewDriver(
    private val view: BudapestView
) : ViewDriver<BudapestState> {
  override fun render(source: Observable<BudapestState>): Disposable {
    return source
        .distinctUntilChanged()
        .subscribe { greet(it) }
  }

  private fun greet(state: BudapestState) {
    if (state == BudapestState.STRANGER) {
      view.greetStranger()
    } else {
      view.greet(state.name)
    }
  }
}
