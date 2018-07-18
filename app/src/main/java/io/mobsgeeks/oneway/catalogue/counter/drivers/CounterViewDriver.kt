package io.mobsgeeks.oneway.catalogue.counter.drivers

import io.mobsgeeks.oneway.catalogue.counter.CounterState
import io.mobsgeeks.oneway.catalogue.counter.CounterView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class CounterViewDriver(private val view: CounterView) {
  fun render(states: Observable<CounterState>): Disposable =
      states
          .map { it.counter }
          .distinctUntilChanged()
          .subscribe { view.showCounter(it) }
}
