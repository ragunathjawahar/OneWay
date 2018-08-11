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
package io.mobsgeeks.oneway.drivers

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * The [ViewDriver] contains business logic for rendering the state on the
 * view. The driver implementation should not contain mutable state.
 *
 * @param S the state to render.
 */
interface ViewDriver<S> {
  /**
   * Updates the view whenever the `source` stream emits a state. View updates
   * can be optimized by filtering state properties and subscribing to those
   * filtered properties individually after applying the
   * [io.reactivex.Observable.distinctUntilChanged] operator.
   *
   * @param source the state stream.
   *
   * @return a [Disposable] for managing the subscription(s). It is also not
   *         unusual to return a [io.reactivex.disposables.CompositeDisposable].
   */
  fun render(source: Observable<S>): Disposable
}
