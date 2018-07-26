package io.mobsgeeks.oneway.catalogue.counter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jakewharton.rxbinding2.view.clicks
import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.MviDelegate
import io.mobsgeeks.oneway.Sink
import io.mobsgeeks.oneway.Source
import io.mobsgeeks.oneway.catalogue.R
import io.mobsgeeks.oneway.catalogue.counter.drivers.CounterViewDriver
import io.mobsgeeks.oneway.catalogue.counter.usecases.CounterUseCases
import io.mobsgeeks.oneway.catalogue.counter.usecases.DecrementUseCase
import io.mobsgeeks.oneway.catalogue.counter.usecases.IncrementUseCase
import io.mobsgeeks.oneway.usecases.DefaultBindingCreatedUseCase
import io.mobsgeeks.oneway.usecases.DefaultBindingRestoredUseCase
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.counter_fragment.*

private const val KEY_STATE = "state"

class CounterActivity : AppCompatActivity(), CounterView {
  private val mviDelegate: MviDelegate<CounterState, CounterState> = MviDelegate()

  private val intentions: CounterIntentions
    get() = CounterIntentions(incrementButton.clicks(), decrementButton.clicks())

  private val createdUseCase: DefaultBindingCreatedUseCase<CounterState>
    get() = DefaultBindingCreatedUseCase(CounterState.ZERO)

  private val restoredUseCase: DefaultBindingRestoredUseCase<CounterState>
    get() = DefaultBindingRestoredUseCase(mviDelegate.timeline)

  private val incrementUseCase: IncrementUseCase
    get() = IncrementUseCase(mviDelegate.timeline)

  private val decrementUseCase: DecrementUseCase
    get() = DecrementUseCase(mviDelegate.timeline)

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

    val state = savedInstanceState?.get(KEY_STATE) as CounterState?
    mviDelegate.restoreState(state)
  }

  override fun onStart() {
    super.onStart()
    val source: Source<CounterState> = object : Source<CounterState> {
      override fun produce(
          bindings: Observable<Binding>,
          timeline: Observable<CounterState>
      ): Observable<CounterState> =
          CounterModel.bind(intentions.stream(), bindings, useCases)
    }

    val sink: Sink<CounterState> = object : Sink<CounterState> {
      override fun consume(source: Observable<CounterState>): Disposable =
          viewDriver.render(source)
    }

    mviDelegate.bind(source, sink)
  }

  override fun onStop() {
    mviDelegate.unbind()
    super.onStop()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    val state = mviDelegate.saveState()
    outState.putParcelable(KEY_STATE, state)
    super.onSaveInstanceState(outState)
  }
}
