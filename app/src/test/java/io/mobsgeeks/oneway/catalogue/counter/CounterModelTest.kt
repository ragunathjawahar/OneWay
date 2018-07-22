package io.mobsgeeks.oneway.catalogue.counter

import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.Binding.CREATED
import io.mobsgeeks.oneway.Binding.RESTORED
import io.mobsgeeks.oneway.catalogue.counter.CounterState.Companion.ZERO
import io.mobsgeeks.oneway.catalogue.counter.usecases.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.junit.After
import org.junit.Before
import org.junit.Test

class CounterModelTest {
  private val intentions = PublishSubject.create<CounterIntention>()
  private val bindings = PublishSubject.create<Binding>()
  private val timeline = PublishSubject.create<CounterState>()
  private val testObserver = TestObserver<CounterState>()
  private val disposable: CompositeDisposable = CompositeDisposable()

  private val useCases = CounterUseCases(
      CreatedUseCase(),
      RestoredUseCase(timeline),
      IncrementUseCase(timeline),
      DecrementUseCase(timeline)
  )

  @Before fun setup() {
    val sharedStates = CounterModel
        .bind(intentions, bindings, useCases)
        .share()
    disposable.addAll(
        sharedStates.subscribe { timeline.onNext(it) },
        sharedStates.subscribeWith(testObserver)
    )
  }

  @After fun teardown() {
    disposable.dispose()
  }

  @Test fun `creating the screen starts with a ZERO state`() {
    // when
    screenIsCreated()

    // then
    assertStates(testObserver, ZERO)
  }

  @Test fun `tapping on + increments the counter by 1`() {
    // when
    startWith(ZERO) {
      increment()
    }

    // then
    val one = ZERO.add(1)
    assertStates(testObserver, one)
  }

  @Test fun `tapping on - decrements the counter by 1`() {
    // when
    startWith(ZERO) {
      decrement()
    }

    // then
    val minusOne = ZERO.add(-1)
    assertStates(testObserver, minusOne)
  }

  @Test fun `restoring the screen preserves the previous state`() {
    // given
    val three = CounterState(3)

    // when
    startWith(three) {
      screenIsRestored()
    }

    // then
    with(testObserver) {
      assertNoErrors()
      assertValues(three)
    }
  }

  private fun screenIsCreated() {
    bindings.onNext(CREATED)
  }

  private fun increment() {
    intentions.onNext(Increment)
  }

  private fun decrement() {
    intentions.onNext(Decrement)
  }

  private fun screenIsRestored() {
    bindings.onNext(RESTORED)
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

  private fun startWith(
      startState: CounterState,
      block: () -> Unit
  ) {
    timeline.onNext(startState)
    block()
  }
}
