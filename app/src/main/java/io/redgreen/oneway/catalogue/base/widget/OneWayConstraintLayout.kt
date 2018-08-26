package io.redgreen.oneway.catalogue.base.widget

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import io.reactivex.Observable
import io.redgreen.oneway.NoOpStateConverter
import io.redgreen.oneway.StateConverter
import io.redgreen.oneway.android.ParcelablePersister
import io.redgreen.oneway.android.barebones.AndroidMviContract
import io.redgreen.oneway.android.barebones.AndroidMviDelegate
import io.redgreen.oneway.android.barebones.Persister
import kotlin.LazyThreadSafetyMode.NONE

private const val KEY_VIEW_STATE = "view_state"

abstract class OneWayConstraintLayout<S : Parcelable> :
    ConstraintLayout,
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

  private val mviDelegate: AndroidMviDelegate<S, S> by lazy(NONE) {
    AndroidMviDelegate(this)
  }

  override val stateConverter: StateConverter<S, S> =
      NoOpStateConverter()

  override val persister: Persister<S> by lazy(NONE) {
    return@lazy ParcelablePersister<S>(this::class.java.name)
  }

  override val timeline: Observable<S> =
      mviDelegate.timeline

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
