package io.mobsgeeks.oneway.catalogue.budapest.drivers

import io.mobsgeeks.oneway.catalogue.budapest.BudapestState
import io.mobsgeeks.oneway.catalogue.budapest.BudapestView
import io.mobsgeeks.oneway.drivers.ViewDriver
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

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
