package io.mobsgeeks.oneway.catalogue.mvi

import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.MviDelegate
import io.mobsgeeks.oneway.Sink
import io.mobsgeeks.oneway.Source
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

private const val KEY_STATE = "state"

// TODO(rj) 31/Jul/18 - This should be part of the 'core-android' module.
abstract class MviActivity<S : Parcelable> : AppCompatActivity() {
  private val mviDelegate: MviDelegate<S, S> = MviDelegate()

  protected val timeline: Observable<S>
    get() = mviDelegate.timeline

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val state = savedInstanceState?.get(KEY_STATE) as S?
    mviDelegate.restoreState(state)
  }

  override fun onStart() {
    super.onStart()
    val source: Source<S> = object : Source<S> {
      override fun produce(
          bindings: Observable<Binding>,
          timeline: Observable<S>
      ): Observable<S> =
          source(bindings, timeline)
    }

    val sink: Sink<S> = object : Sink<S> {
      override fun consume(source: Observable<S>): Disposable =
          sink(source)
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

  protected abstract fun source(
      bindings: Observable<Binding>,
      timeline: Observable<S>
  ): Observable<S>

  protected abstract fun sink(source: Observable<S>): Disposable
}
