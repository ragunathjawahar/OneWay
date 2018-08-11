package io.mobsgeeks.oneway

import io.mobsgeeks.oneway.SourceEvent.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlin.LazyThreadSafetyMode.NONE

class MviDelegate<S, P>(private val stateConverter: StateConverter<S, P>) {
  private val compositeDisposable = CompositeDisposable()
  private val sourceEventsSubject = PublishSubject.create<SourceEvent>()
  private val timelineSubject = BehaviorSubject.create<S>()

  val sourceEvents: Observable<SourceEvent> by lazy(NONE) {
    sourceEventsSubject.hide()
  }

  // TODO(rj) 22/Jul/18 - Mention that this should NEVER be a primary stream in the KDoc, because that would cause an infinite loop.
  val timeline: Observable<S> by lazy(NONE) {
    timelineSubject.hide().share()
  }

  fun bind(source: Source<S>, sink: Sink<S>) {
    val sharedStates = source.produce(sourceEvents, timeline).publish()
    compositeDisposable.addAll(
        sink.consume(sharedStates),
        sharedStates.subscribe { timelineSubject.onNext(it) },
        sharedStates.connect()
    )
    val sourceEvent = if (timelineSubject.value == null) CREATED else RESTORED
    sourceEventsSubject.onNext(sourceEvent)
  }

  fun unbind() {
    if (compositeDisposable.size() > 0) {
      sourceEventsSubject.onNext(DESTROYED)
      compositeDisposable.clear()
    }
  }

  fun saveState(): P? {
    val state = timelineSubject.value
    return state?.let { stateConverter.to(state) }
  }

  fun restoreState(persistentState: P?) {
    persistentState?.let { timelineSubject.onNext(stateConverter.from(it)) }
  }
}
