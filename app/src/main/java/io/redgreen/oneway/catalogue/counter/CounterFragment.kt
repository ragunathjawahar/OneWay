package io.redgreen.oneway.catalogue.counter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.android.OneWayFragment
import io.redgreen.oneway.catalogue.R
import io.redgreen.oneway.catalogue.base.extensions.fastLazy
import io.redgreen.oneway.catalogue.counter.drivers.CounterViewDriver
import io.redgreen.oneway.catalogue.counter.usecases.CounterUseCases
import kotlinx.android.synthetic.main.counter_fragment.*

class CounterFragment : OneWayFragment<CounterState>(), CounterView {
  private val intentions by fastLazy {
    CounterIntentions(
        incrementButton.clicks(),
        decrementButton.clicks()
    )
  }

  private val useCases by fastLazy {
    CounterUseCases(sourceCopy)
  }

  private val viewDriver by fastLazy {
    CounterViewDriver(this)
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View =
      inflater.inflate(R.layout.counter_fragment, container, false)

  override fun source(
      sourceLifecycleEvents: Observable<SourceLifecycleEvent>,
      sourceCopy: Observable<CounterState>
  ): Observable<CounterState> =
      CounterModel.createSource(intentions.stream(), sourceLifecycleEvents, useCases)

  override fun sink(source: Observable<CounterState>): Disposable =
      viewDriver.render(source)

  override fun showCounter(counter: Int) {
    counterTextView.text = counter.toString()
  }
}
