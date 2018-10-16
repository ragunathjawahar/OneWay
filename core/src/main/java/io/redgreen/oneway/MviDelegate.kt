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
package io.redgreen.oneway

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.SourceEvent.*
import kotlin.LazyThreadSafetyMode.NONE

/**
 * Manages the subscription between a [Source] and a [Sink]. This includes dispatching
 * appropriate lifecycle events and managing state.
 */
class MviDelegate<S, P>(private val stateConverter: StateConverter<S, P>) {
  private val compositeDisposable = CompositeDisposable()
  private val sourceEventsSubject = PublishSubject.create<SourceEvent>()
  private val sourceCopySubject = BehaviorSubject.create<S>()

  /** The lifecycle stream. */
  val sourceEvents: Observable<SourceEvent> by lazy(NONE) {
    sourceEventsSubject.hide()
  }

  /**
   * The state relay that grants access to the current state of the model. This stream
   * should not be used as a primary stream from within the model. Using it as a primary
   * stream may cause an infinite loop within the system.
   */
  val sourceCopy: Observable<S> by lazy(NONE) {
    sourceCopySubject.hide().share()
  }

  /**
   * Creates a subscription between the [Source] and the [Sink]. After the subscription
   * has been established, it also dispatches either a [SourceEvent.CREATED] or a
   * [SourceEvent.RESTORED] event depending upon the state of the system.
   */
  fun bind(source: Source<S>, sink: Sink<S>) {
    val sharedStates = source.produce(sourceEvents, sourceCopy).publish()
    compositeDisposable.addAll(
        sink.consume(sharedStates),
        sharedStates.subscribe { sourceCopySubject.onNext(it) },
        sharedStates.connect()
    )
    val sourceEvent = if (sourceCopySubject.value == null) CREATED else RESTORED
    sourceEventsSubject.onNext(sourceEvent)
  }

  /**
   * Disposes the subscription between the [Source] and the [Sink]. It also dispatches
   * a [SourceEvent.DESTROYED] event before the subscription is cleared.
   */
  fun unbind() {
    if (compositeDisposable.size() > 0) {
      sourceEventsSubject.onNext(DESTROYED)
      compositeDisposable.clear()
    }
  }

  /**
   * Gets the current state of the source. This function is usually called after the
   * subscription between the [Source] and the [Sink] has been disposed.
   */
  fun getState(): P? {
    val state = sourceCopySubject.value
    return state?.let { stateConverter.to(state) }
  }

  /**
   * Puts a state into the [sourceCopy]. This function is usually called before
   * re-establishing the subscription between the [Source] and the [Sink] after an
   * [unbind].
   */
  fun putState(persistentState: P?) {
    persistentState?.let { sourceCopySubject.onNext(stateConverter.from(it)) }
  }
}
