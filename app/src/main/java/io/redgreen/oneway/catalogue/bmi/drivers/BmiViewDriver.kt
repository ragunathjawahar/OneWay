package io.redgreen.oneway.catalogue.bmi.drivers

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.redgreen.oneway.catalogue.bmi.BmiState
import io.redgreen.oneway.catalogue.bmi.BmiView
import io.redgreen.oneway.drivers.ViewDriver

class BmiViewDriver(private val view: BmiView) : ViewDriver<BmiView, BmiState>(view) {
  override fun render(source: Observable<BmiState>): Disposable {
    val compositeDisposable = CompositeDisposable()
    val connectableStates = source.publish()

    compositeDisposable.addAll(
        showBmi(connectableStates),
        showCategory(connectableStates),
        showWeight(connectableStates),
        showHeight(connectableStates),
        showMeasurementSystem(connectableStates),
        connectableStates.connect()
    )

    return compositeDisposable
  }

  private fun showBmi(states: Observable<BmiState>): Disposable {
    return states
        .map { it.bmi }
        .distinctUntilChanged()
        .subscribe { view.showBmi(it) }
  }

  private fun showCategory(states: Observable<BmiState>): Disposable {
    return states
        .map { it.category }
        .distinctUntilChanged()
        .subscribe { view.showCategory(it) }
  }

  private fun showWeight(states: Observable<BmiState>): Disposable {
    return states
        .map { it.height to it.measurementSystem }
        .distinctUntilChanged()
        .subscribe { (height, measurementSystem) ->
          view.showHeight(height, measurementSystem)
        }
  }

  private fun showHeight(states: Observable<BmiState>): Disposable {
    return states
        .map { it.weight to it.measurementSystem }
        .distinctUntilChanged()
        .subscribe { (weight, measurementSystem) ->
          view.showWeight(weight, measurementSystem)
        }
  }

  private fun showMeasurementSystem(states: Observable<BmiState>): Disposable {
    return states
        .map { it.measurementSystem }
        .distinctUntilChanged()
        .subscribe { view.showMeasurementSystem(it) }
  }
}
