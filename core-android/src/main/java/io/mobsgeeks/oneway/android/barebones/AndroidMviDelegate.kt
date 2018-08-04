package io.mobsgeeks.oneway.android.barebones

import android.os.Bundle
import io.mobsgeeks.oneway.MviDelegate
import io.mobsgeeks.oneway.Sink
import io.mobsgeeks.oneway.Source
import io.mobsgeeks.oneway.SourceEvent
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlin.LazyThreadSafetyMode.NONE

class AndroidMviDelegate<S, P>(
    private val androidMviContract: AndroidMviContract<S, P>
) {
  private val mviDelegate: MviDelegate<S, P> by lazy(NONE) {
    MviDelegate(androidMviContract.stateSerializer)
  }

  val timeline: Observable<S> =
      mviDelegate.timeline

  fun bind() {
    mviDelegate.bind(createSource(), createSink())
  }

  fun unbind() {
    mviDelegate.unbind()
  }

  fun saveState(bundle: Bundle) {
    val state = mviDelegate.saveState()
    state?.let { androidMviContract.persister.write(state, bundle) }
  }

  fun restoreState(bundle: Bundle?) {
    bundle?.let {
      val persistedState = androidMviContract.persister.read(it)
      mviDelegate.restoreState(persistedState)
    }
  }

  private fun createSource(): Source<S> {
    return object : Source<S> {
      override fun produce(
          sourceEvents: Observable<SourceEvent>,
          timeline: Observable<S>
      ): Observable<S> =
          androidMviContract.source(sourceEvents, timeline)
    }
  }

  private fun createSink(): Sink<S> {
    return object : Sink<S> {
      override fun consume(source: Observable<S>): Disposable =
          androidMviContract.sink(source)
    }
  }
}
