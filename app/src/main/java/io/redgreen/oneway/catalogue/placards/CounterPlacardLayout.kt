package io.redgreen.oneway.catalogue.placards

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.redgreen.oneway.NoOpStateConverter
import io.redgreen.oneway.SourceEvent
import io.redgreen.oneway.StateConverter
import io.redgreen.oneway.android.ParcelablePersister
import io.redgreen.oneway.android.barebones.AndroidMviContract
import io.redgreen.oneway.android.barebones.AndroidMviDelegate
import io.redgreen.oneway.android.barebones.Persister
import io.redgreen.oneway.catalogue.counter.CounterIntentionsGroup
import io.redgreen.oneway.catalogue.counter.CounterModel
import io.redgreen.oneway.catalogue.counter.CounterState
import io.redgreen.oneway.catalogue.counter.CounterView
import io.redgreen.oneway.catalogue.counter.drivers.CounterViewDriver
import io.redgreen.oneway.catalogue.counter.usecases.CounterUseCases
import kotlinx.android.synthetic.main.counter_placard.view.*

class CounterPlacardLayout :
    RelativeLayout,
    AndroidMviContract<CounterState, CounterState>,
    CounterView
{
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

  @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
  constructor(
      context: Context,
      attrs: AttributeSet,
      defStyleAttr: Int,
      defStyleRes: Int
  ): super(context, attrs, defStyleAttr, defStyleRes)

  override val timeline: Observable<CounterState>
    get() = mviDelegate.timeline

  override val stateConverter: StateConverter<CounterState, CounterState>
    get() = NoOpStateConverter()

  override val persister: Persister<CounterState>
    get() = ParcelablePersister()

  private val mviDelegate: AndroidMviDelegate<CounterState, CounterState> by lazy(LazyThreadSafetyMode.NONE) {
    AndroidMviDelegate(this)
  }

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

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    mviDelegate.bind()
  }

  override fun onDetachedFromWindow() {
    mviDelegate.unbind()
    super.onDetachedFromWindow()
  }

  override fun onSaveInstanceState(): Parcelable {
    val outState = Bundle()

    val viewState = super.onSaveInstanceState()
    outState.putParcelable("view_state", viewState)

    mviDelegate.saveState(outState)

    return outState
  }

  override fun onRestoreInstanceState(state: Parcelable?) {
    mviDelegate.restoreState(state as Bundle)
    super.onRestoreInstanceState(state.getParcelable("view_state"))
  }
}
