package io.mobsgeeks.oneway.android

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import io.mobsgeeks.oneway.*
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlin.LazyThreadSafetyMode.NONE

abstract class BareBonesOneWayFragment<S, P> : Fragment() {
  private val mviDelegate: MviDelegate<S, P> by lazy(NONE) {
    MviDelegate(stateSerializer)
  }

  protected val timeline: Observable<S>
    get() = mviDelegate.timeline

  protected abstract val stateSerializer: StateSerializer<S, P>

  protected abstract val persister: Persister<P>

  @CallSuper override fun onStart() {
    super.onStart()
    mviDelegate.bind(createSource(), createSink())
  }

  @CallSuper override fun onStop() {
    mviDelegate.unbind()
    super.onStop()
  }

  @CallSuper override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    val state = mviDelegate.saveState()
    state?.let { persister.write(state, outState) }
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    savedInstanceState?.let {
      val persistedState = persister.read(savedInstanceState)
      mviDelegate.restoreState(persistedState)
    }
  }

  protected abstract fun source(
      sourceEvents: Observable<SourceEvent>,
      timeline: Observable<S>
  ): Observable<S>

  protected abstract fun sink(source: Observable<S>): Disposable

  private fun createSource(): Source<S> {
    return object : Source<S> {
      override fun produce(
          sourceEvents: Observable<SourceEvent>,
          timeline: Observable<S>
      ): Observable<S> =
          source(sourceEvents, timeline)
    }
  }

  private fun createSink(): Sink<S> {
    return object : Sink<S> {
      override fun consume(source: Observable<S>): Disposable =
          sink(source)
    }
  }
}
