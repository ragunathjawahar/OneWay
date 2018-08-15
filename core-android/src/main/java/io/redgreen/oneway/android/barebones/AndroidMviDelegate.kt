/*
 * Copyright (C) 2018 Ragunath Jawahar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.redgreen.oneway.android.barebones

import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.redgreen.oneway.MviDelegate
import io.redgreen.oneway.Sink
import io.redgreen.oneway.Source
import io.redgreen.oneway.SourceEvent
import kotlin.LazyThreadSafetyMode.NONE

/** A [MviDelegate] implementation for the Android platform. */
class AndroidMviDelegate<S, P>(
    private val androidMviContract: AndroidMviContract<S, P>
) {
  private val mviDelegate: MviDelegate<S, P> by lazy(NONE) {
    MviDelegate(androidMviContract.stateConverter)
  }

  /**
   * Grants access to the latest state. Use cases should not use the
   * `timeline` stream as a primary stream.
   */
  val timeline: Observable<S> =
      mviDelegate.timeline

  /** Sets up the subscription between the source and the sink. */
  fun bind() {
    mviDelegate.bind(createSource(), createSink())
  }

  /** Disposes the subscription between the source and the sink. */
  fun unbind() {
    mviDelegate.unbind()
  }

  /** Gets the current state and stores this into the bundle. */
  fun saveState(bundle: Bundle) {
    val state = mviDelegate.getState()
    state?.let { androidMviContract.persister.write(state, bundle) }
  }

  /** Retrieves the state object from the bundle and restores it. */
  fun restoreState(bundle: Bundle?) {
    bundle?.let {
      val persistedState = androidMviContract.persister.read(it)
      mviDelegate.putState(persistedState)
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
