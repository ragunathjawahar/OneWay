package io.mobsgeeks.oneway.catalogue.counter.drivers

import io.mobsgeeks.oneway.catalogue.counter.CounterState
import io.mobsgeeks.oneway.catalogue.counter.CounterView
import io.mobsgeeks.oneway.drivers.ViewDriver
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class CounterViewDriver(private val view: CounterView) : ViewDriver<CounterState> {
  override fun render(source: Observable<CounterState>): Disposable =
      source
          .map { it.counter }
          .distinctUntilChanged()
          .subscribe { view.showCounter(it) }
}
