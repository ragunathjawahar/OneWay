package io.redgreen.oneway.catalogue.base.widget

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.widget.RelativeLayout
import io.reactivex.Observable
import io.redgreen.oneway.NoOpStateConverter
import io.redgreen.oneway.StateConverter
import io.redgreen.oneway.android.ParcelablePersister
import io.redgreen.oneway.android.barebones.AndroidMviContract
import io.redgreen.oneway.android.barebones.AndroidMviDelegate
import io.redgreen.oneway.android.barebones.Persister

private const val KEY_VIEW_STATE = "view_state"

abstract class OneWayRelativeLayout<S : Parcelable> :
    RelativeLayout,
    AndroidMviContract<S, S> {
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

  override val timeline: Observable<S>
    get() = mviDelegate.timeline

  override val stateConverter: StateConverter<S, S>
    get() = NoOpStateConverter()

  override val persister: Persister<S>
    get() = ParcelablePersister()

  private val mviDelegate: AndroidMviDelegate<S, S> by lazy(LazyThreadSafetyMode.NONE) {
    AndroidMviDelegate(this)
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
    val viewState = super.onSaveInstanceState()

    val outState = Bundle()
    outState.putParcelable(KEY_VIEW_STATE, viewState)
    mviDelegate.saveState(outState)
    return outState
  }

  override fun onRestoreInstanceState(state: Parcelable?) {
    mviDelegate.restoreState(state as Bundle)
    super.onRestoreInstanceState(state.getParcelable(KEY_VIEW_STATE))
  }
}
