package io.mobsgeeks.oneway

import io.mobsgeeks.oneway.Binding.*
import io.reactivex.BackpressureStrategy.LATEST
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlin.LazyThreadSafetyMode.NONE

class MviDelegate<S, P>(private val persister: Persister<S, P>) {
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
      sink: (Observable<S>) -> Disposable
  ) {
    val sharedStates = source.produce(bindings, timeline).share()
    compositeDisposable.addAll(
        sink(sharedStates),
        sharedStates.subscribe { timelineSubject.onNext(it) }
    )
    bindingsSubject.onNext(CREATED)
  }

  fun restoreState(persistentState: P?) {
    if (persistentState == null) {
      bindingsSubject.onNext(CREATED)
    } else {
      timelineSubject.onNext(persister.deserialize(persistentState))
      bindingsSubject.onNext(RESTORED)
    }
  }

  fun saveState(): P? {
    val state = timelineSubject.value
    return state?.let { persister.serialize(state) }
  }

  fun unbind() {
    if (compositeDisposable.size() > 0) {
      bindingsSubject.onNext(DESTROYED)
      compositeDisposable.clear()
    }
  }
}
