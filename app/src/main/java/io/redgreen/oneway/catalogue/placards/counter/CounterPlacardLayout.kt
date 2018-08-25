package io.redgreen.oneway.catalogue.placards.counter

import android.content.Context
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.redgreen.oneway.SourceEvent
import io.redgreen.oneway.catalogue.base.widget.OneWayRelativeLayout
import io.redgreen.oneway.catalogue.counter.CounterIntentionsGroup
import io.redgreen.oneway.catalogue.counter.CounterModel
import io.redgreen.oneway.catalogue.counter.CounterState
import io.redgreen.oneway.catalogue.counter.CounterView
import io.redgreen.oneway.catalogue.counter.drivers.CounterViewDriver
import io.redgreen.oneway.catalogue.counter.usecases.CounterUseCases
import kotlinx.android.synthetic.main.counter_placard.view.*

class CounterPlacardLayout :
    OneWayRelativeLayout<CounterState>,
    CounterView {
  constructor(
      context: Context
  ) : super(context)

  constructor(
      context: Context,
      attrs: AttributeSet
  ): super(context, attrs)

  constructor(
      context: Context,
      attrs: AttributeSet,
      defStyleAttr: Int
  ): super(context, attrs, defStyleAttr)

  @RequiresApi(LOLLIPOP)
  constructor(
      context: Context,
      attrs: AttributeSet,
      defStyleAttr: Int,
      defStyleRes: Int
  ): super(context, attrs, defStyleAttr, defStyleRes)

  private val intentionsGroup: CounterIntentionsGroup
    get() = CounterIntentionsGroup(
        incrementButton.clicks(),
        decrementButton.clicks()
    )

  private val useCases: CounterUseCases
    get() = CounterUseCases(CounterState.ZERO, timeline)

  private val viewDriver: CounterViewDriver
    get() = CounterViewDriver(this)

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
