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
package io.redgreen.oneway.usecases

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.SourceLifecycleEvent.RESTORED

/**
 * Convenience class that emits the last known state from the `sourceCopy` when
 * it receives a [SourceLifecycleEvent.RESTORED] event.
 */
class SourceRestoredUseCase<S>(
    private val sourceCopy: Observable<S>
) : ObservableTransformer<SourceLifecycleEvent, S> {
  override fun apply(sourceLifecycleEvents: Observable<SourceLifecycleEvent>): Observable<S> {
    val selectStateFunction = BiFunction<SourceLifecycleEvent, S, S> { _, state -> state }
    return sourceLifecycleEvents
        .filter { it == RESTORED }
        .withLatestFrom(sourceCopy, selectStateFunction)
  }
}
