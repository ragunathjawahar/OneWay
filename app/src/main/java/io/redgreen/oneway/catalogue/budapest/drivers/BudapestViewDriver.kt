package io.redgreen.oneway.catalogue.budapest.drivers

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.redgreen.oneway.catalogue.budapest.BudapestState
import io.redgreen.oneway.catalogue.budapest.BudapestView
import io.redgreen.oneway.catalogue.budapest.GreeterState
import io.redgreen.oneway.catalogue.budapest.StrangerState
import io.redgreen.oneway.drivers.ViewDriver

class BudapestViewDriver(
    private val view: BudapestView
) : ViewDriver<BudapestView, BudapestState>(view) {
  override fun render(source: Observable<BudapestState>): Disposable {
    return source
        .distinctUntilChanged()
        .subscribe { greet(it) }
  }

  private fun greet(state: BudapestState) {
    when (state) {
      is StrangerState -> view.greetStranger()
      is GreeterState  -> view.greet(state.name)
    }
  }
}
