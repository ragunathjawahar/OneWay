package io.redgreen.oneway.catalogue.counter.drivers

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.redgreen.oneway.catalogue.counter.CounterState
import io.redgreen.oneway.catalogue.counter.CounterView
import io.redgreen.oneway.drivers.ViewDriver

class CounterViewDriver(private val view: CounterView) : ViewDriver<CounterView, CounterState>(view) {
  override fun render(source: Observable<CounterState>): Disposable =
      source
          .map { it.counter }
          .distinctUntilChanged()
          .subscribe { view.showCounter(it) }
}
