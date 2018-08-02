package io.mobsgeeks.oneway.catalogue.counter

import android.os.Bundle
import com.jakewharton.rxbinding2.view.clicks
import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.catalogue.R
import io.mobsgeeks.oneway.catalogue.counter.drivers.CounterViewDriver
import io.mobsgeeks.oneway.catalogue.counter.usecases.CounterUseCases
import io.mobsgeeks.oneway.catalogue.counter.usecases.DecrementUseCase
import io.mobsgeeks.oneway.catalogue.counter.usecases.IncrementUseCase
import io.mobsgeeks.oneway.catalogue.mvi.MviActivity
import io.mobsgeeks.oneway.usecases.DefaultSourceCreatedUseCase
import io.mobsgeeks.oneway.usecases.DefaultSourceRestoredUseCase
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.counter_fragment.*

class CounterActivity : MviActivity<CounterState>(), CounterView {
  private val intentionsGroup: CounterIntentionsGroup
    get() = CounterIntentionsGroup(incrementButton.clicks(), decrementButton.clicks())

  private val createdUseCase: DefaultSourceCreatedUseCase<CounterState>
    get() = DefaultSourceCreatedUseCase(CounterState.ZERO)

  private val restoredUseCase: DefaultSourceRestoredUseCase<CounterState>
    get() = DefaultSourceRestoredUseCase(timeline)

  private val incrementUseCase: IncrementUseCase
    get() = IncrementUseCase(timeline)

  private val decrementUseCase: DecrementUseCase
    get() = DecrementUseCase(timeline)

  private val useCases: CounterUseCases
    get() = CounterUseCases(
        createdUseCase,
        restoredUseCase,
        incrementUseCase,
        decrementUseCase
    )

  private val viewDriver: CounterViewDriver
    get() = CounterViewDriver(this)

  override fun showCounter(counter: Int) {
    counterTextView.text = counter.toString()
  }

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
}
