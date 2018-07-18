package io.mobsgeeks.oneway

import io.mobsgeeks.oneway.Binding.*
import io.reactivex.BackpressureStrategy.LATEST
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlin.LazyThreadSafetyMode.NONE

class MviDelegate<S, P>(private val stateSerializer: StateSerializer<S, P>) {
  private val compositeDisposable = CompositeDisposable()
  private val bindingsSubject = PublishSubject.create<Binding>()
  private val timelineSubject = BehaviorSubject.create<S>()

  val bindings: Observable<Binding> by lazy(NONE) {
    bindingsSubject.toFlowable(LATEST).toObservable()
  }

  internal val timeline: Observable<S> by lazy(NONE) {
    timelineSubject.toFlowable(LATEST).toObservable().share()
  }

  fun bind(
      source: Source<S>,
      sink: Sink<S>
  ) {
    val sharedStates = source.produce(bindings, timeline).publish()
    compositeDisposable.addAll(
        sink.consume(sharedStates),
        sharedStates.subscribe { timelineSubject.onNext(it) },
        sharedStates.connect()
    )
    val binding = if (timelineSubject.value == null) CREATED else RESTORED
    bindingsSubject.onNext(binding)
  }

  fun restoreState(persistentState: P?) {
    persistentState?.let { timelineSubject.onNext(stateSerializer.deserialize(it)) }
  }

  fun saveState(): P? {
    val state = timelineSubject.value
    return state?.let { stateSerializer.serialize(state) }
  }

  fun unbind() {
    if (compositeDisposable.size() > 0) {
      bindingsSubject.onNext(DESTROYED)
      compositeDisposable.clear()
    }
  }
}
