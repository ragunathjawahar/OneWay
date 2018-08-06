package io.mobsgeeks.oneway.catalogue.counter

import android.os.Bundle
import com.jakewharton.rxbinding2.view.clicks
import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.android.OneWayActivity
import io.mobsgeeks.oneway.catalogue.R
import io.mobsgeeks.oneway.catalogue.counter.CounterState.Companion.ZERO
import io.mobsgeeks.oneway.catalogue.counter.drivers.CounterViewDriver
import io.mobsgeeks.oneway.catalogue.counter.usecases.CounterUseCases
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.counter_fragment.*

class CounterActivity : OneWayActivity<CounterState>(), CounterView {
  private val intentionsGroup: CounterIntentionsGroup
    get() = CounterIntentionsGroup(
        incrementButton.clicks(),
        decrementButton.clicks()
    )

  private val useCases: CounterUseCases
    get() = CounterUseCases(ZERO, timeline)

  private val viewDriver: CounterViewDriver
    get() = CounterViewDriver(this)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.counter_fragment)
  }

  override fun source(
      sourceEvents: Observable<SourceEvent>,
      timeline: Observable<CounterState>
  ): Observable<CounterState> =
      CounterModel.createSource(intentionsGroup.intentions(), sourceEvents, useCases)

  override fun sink(source: Observable<CounterState>): Disposable =
      viewDriver.render(source)

  override fun showCounter(counter: Int) {
    counterTextView.text = counter.toString()
  }
}
