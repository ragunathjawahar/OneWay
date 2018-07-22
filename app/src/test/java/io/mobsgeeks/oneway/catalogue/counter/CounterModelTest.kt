package io.mobsgeeks.oneway.catalogue.counter

import io.mobsgeeks.oneway.Binding
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

  private val useCases = CounterUseCases(
      CreatedUseCase(),
      RestoredUseCase(timeline),
      IncrementUseCase(timeline),
      DecrementUseCase(timeline)
  )

  private val testObserver = TestObserver<CounterState>()

  private val disposable: CompositeDisposable = CompositeDisposable()

  @Before
  fun setup() {
    val sharedStates = CounterModel
        .bind(intentions, bindings, useCases)
        .share()
    disposable.addAll(
        sharedStates.subscribe { timeline.onNext(it) },
        sharedStates.subscribeWith(testObserver)
    )
  }

  @After
  fun teardown() {
    disposable.dispose()
  }

  @Test fun `start with a ZERO state`() {
    // when
    bindings.onNext(Binding.CREATED)

    // then
    assertStates(testObserver, ZERO)
  }

  @Test fun `tapping on + increments the counter by 1`() {
    // given
    bindings.onNext(Binding.CREATED)

    // when
    intentions.onNext(Increment)

    // then
    val one = ZERO.add(1)
    assertStates(testObserver, ZERO, one)
  }

  @Test fun `tapping on - decrements the counter by 1`() {
    // given
    bindings.onNext(Binding.CREATED)

    // when
    intentions.onNext(Decrement)

    // then
    val minusOne = ZERO.add(-1)
    assertStates(testObserver, ZERO, minusOne)
  }

  @Test fun `restoring the screen preserves last known state`() {
    // given
    bindings.onNext(Binding.CREATED)
    intentions.onNext(Increment)

    // when
    bindings.onNext(Binding.RESTORED)

    // then
    val one = ZERO.add(1)
    with(testObserver) {
      assertNoErrors()
      assertValues(ZERO, one, one)
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
