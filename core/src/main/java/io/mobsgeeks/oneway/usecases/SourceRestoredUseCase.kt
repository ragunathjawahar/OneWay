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
package io.mobsgeeks.oneway.usecases

import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.SourceEvent.RESTORED
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction

/**
 * Convenience class that emits the last known state from the `timeline` when
 * it receives `SourceEvent.RESTORED`.
 */
class SourceRestoredUseCase<S>(
    private val timeline: Observable<S>
) : ObservableTransformer<SourceEvent, S> {
  override fun apply(sourceEvents: Observable<SourceEvent>): Observable<S> {
    val selectStateFunction = BiFunction<SourceEvent, S, S> { _, state -> state }
    return sourceEvents
        .filter { it == RESTORED }
        .withLatestFrom(timeline, selectStateFunction)
  }
}
