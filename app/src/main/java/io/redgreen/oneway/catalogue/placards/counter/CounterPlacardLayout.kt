package io.redgreen.oneway.catalogue.placards.counter

import android.content.Context
import android.util.AttributeSet
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.catalogue.base.extensions.fastLazy
import io.redgreen.oneway.catalogue.base.widget.OneWayConstraintLayout
import io.redgreen.oneway.catalogue.counter.CounterIntentions
import io.redgreen.oneway.catalogue.counter.CounterModel
import io.redgreen.oneway.catalogue.counter.CounterState
import io.redgreen.oneway.catalogue.counter.CounterView
import io.redgreen.oneway.catalogue.counter.drivers.CounterViewDriver
import io.redgreen.oneway.catalogue.counter.usecases.CounterUseCases
import kotlinx.android.synthetic.main.counter_placard.view.*

class CounterPlacardLayout :
    OneWayConstraintLayout<CounterState>,
    CounterView {
  constructor(
      context: Context
  ) : super(context)

  constructor(
      context: Context,
      attrs: AttributeSet
  ) : super(context, attrs)

  constructor(
      context: Context,
      attrs: AttributeSet,
      defStyleAttr: Int
  ) : super(context, attrs, defStyleAttr)

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
