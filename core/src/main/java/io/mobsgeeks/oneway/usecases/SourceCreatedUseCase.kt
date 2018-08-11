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
import io.mobsgeeks.oneway.SourceEvent.CREATED
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

/**
 * Convenience class that emits an `initialState` when it receives a
 * created `SourceEvent`.
 */
class SourceCreatedUseCase<S>(
    private val initialState: S
) : ObservableTransformer<SourceEvent, S> {
  override fun apply(sourceEvents: Observable<SourceEvent>): ObservableSource<S> {
    return sourceEvents
        .filter { it == CREATED }
        .map { initialState }
  }
}
