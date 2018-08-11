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
package io.mobsgeeks.oneway.android.barebones

import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.StateConverter
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * Contract for using MVI in Android. Components that make use of the
 * [AndroidMviDelegate] must implement this contract. Currently, a `Fragment`
 * and `Activity` are provided out of the box. However, similar capabilities
 * can be extended to Android views as well.
 *
 * @see BareBonesOneWayActivity
 * @see BareBonesOneWayFragment
 *
 * @param S a state.
 * @param P persistable state.
 */
interface AndroidMviContract<S, P> {
  /**
   * Provides access to the current state. This property should delegated to
   * the `MviDelegate` provided by the [AndroidMviDelegate].
   */
  val timeline: Observable<S>

  /**
   * Responsible for converting the state into a representation suitable
   * for persistence.
   */
  val stateConverter: StateConverter<S, P>

  /** Used to save and restore the persistable state object. */
  val persister: Persister<P>

  /** Provides `sourceEvents` and `timeline` to the model. */
  fun source(
      sourceEvents: Observable<SourceEvent>,
      timeline: Observable<S>
  ): Observable<S>

  /**
   * Allows source events to be consumed. The consumer is usually a
   * [io.mobsgeeks.oneway.drivers.ViewDriver].
   */
  fun sink(source: Observable<S>): Disposable
}
