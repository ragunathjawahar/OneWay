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
package io.mobsgeeks.oneway

import io.reactivex.Observable

/** Producer of the state stream. */
interface Source<S> {
  /**
   * Produces a state stream that is handed over to a `Sink`, the consumer.
   *
   * @param sourceEvents a stream of lifecycle events.
   * @param timeline a state stream that grants access to the most recent state.
   *
   * @return a state stream.
   */
  fun produce(sourceEvents: Observable<SourceEvent>, timeline: Observable<S>): Observable<S>
}
