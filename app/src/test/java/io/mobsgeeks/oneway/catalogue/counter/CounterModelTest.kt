package io.mobsgeeks.oneway.catalogue.counter

import io.mobsgeeks.oneway.*
import io.mobsgeeks.oneway.catalogue.counter.CounterState.Companion.ZERO
import io.mobsgeeks.oneway.catalogue.counter.usecases.*
import io.reactivex.BackpressureStrategy.LATEST
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class CounterModelTest {
  private val intentionsSubject = PublishSubject.create<CounterIntention>()
  private val intentions = intentionsSubject
      .toFlowable(LATEST)
      .toObservable()

  private val stateSerializer = object : StateSerializer<CounterState, CounterState> {
    override fun serialize(state: CounterState): CounterState = state
    override fun deserialize(persistentState: CounterState): CounterState = persistentState
  }

  private val mviDelegate = MviDelegate(stateSerializer)

  private val timeline = mviDelegate.timeline

  private val useCases = CounterUseCases(
      CreatedUseCase(),
      RestoredUseCase(timeline),
      IncrementUseCase(timeline),
      DecrementUseCase(timeline)
  )

  private val source = object : Source<CounterState> {
    override fun produce(
        bindings: Observable<Binding>,
        timeline: Observable<CounterState>
    ): Observable<CounterState> =
        CounterModel.bind(intentions, bindings, useCases)
  }

  private val testObserver = TestObserver<CounterState>()

  private val sink = object : Sink<CounterState> {
    override fun consume(source: Observable<CounterState>): Disposable =
        source.subscribeWith(testObserver)
  }

  @Test fun `start with a ZERO state`() {
    // when
    mviDelegate.bind(source, sink)

    // then
    assertStates(testObserver, ZERO)
  }

  @Test fun `tapping on + increments the counter by 1`() {
    // given
    mviDelegate.bind(source, sink)

    // when
    intentionsSubject.onNext(Increment)

    // then
    val one = ZERO.add(1)
    assertStates(testObserver, ZERO, one)
  }

  @Test fun `tapping on - decrements the counter by 1`() {
    // given
    mviDelegate.bind(source, sink)

    // when
    intentionsSubject.onNext(Decrement)

    // then
    val minusOne = ZERO.add(-1)
    assertStates(testObserver, ZERO, minusOne)
  }

  @Test fun `restoring the screen preserves last known state`() {
    // given
    mviDelegate.bind(source, sink)

    // when - emit events
    intentionsSubject.onNext(Increment)
    intentionsSubject.onNext(Increment)
    intentionsSubject.onNext(Increment)

    // when - unbind
    val savedState = mviDelegate.saveState()
    mviDelegate.unbind()

    // when - restore state and binding
    val newTestObserver = TestObserver<CounterState>()
    val newSink = object : Sink<CounterState> {
      override fun consume(source: Observable<CounterState>): Disposable =
          source.subscribeWith(newTestObserver)
    }
    mviDelegate.restoreState(savedState)
    mviDelegate.bind(source, newSink)

    // then
    val three = CounterState(3)
    with(newTestObserver) {
      assertNoErrors()
      assertValues(three)
    }
  }

  private fun assertStates(
      testObserver: TestObserver<CounterState>,
      vararg states: CounterState
  ) {
    with(testObserver) {
      assertNoErrors()
      assertValues(*states)
      assertNotTerminated()
    }
  }
}
