package io.redgreen.oneway.catalogue.counter

import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.catalogue.counter.CounterState.Companion.ZERO
import io.redgreen.oneway.catalogue.counter.usecases.CounterUseCases
import io.redgreen.oneway.test.MviTestDelegate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CounterModelTest {
  private val intentions = PublishSubject.create<CounterIntention>()
  private val initialState = ZERO

  private val mviTestDelegate = MviTestDelegate<CounterState> { sourceEvents, timeline ->
    CounterModel.createSource(
        intentions,
        sourceEvents,
        CounterUseCases(initialState, timeline)
    )
  }

  @Test fun `creating the source starts with an initial state`() {
    // when
    mviTestDelegate.sourceIsCreated()

    // then
    mviTestDelegate.assertStates(initialState)
  }

  @Test fun `restoring the source restores the last known state`() {
    // given
    val lastKnownState = CounterState(3)

    // when
    mviTestDelegate.startWith(lastKnownState) {
      mviTestDelegate.sourceIsDestroyed()
      mviTestDelegate.sourceIsRestored()
    }

    // then
    mviTestDelegate.assertStates(lastKnownState)
  }

  @DisplayName("after source is created")
  @Nested inner class AfterSourceIsCreated {
    @BeforeEach fun setup() {
      mviTestDelegate.startWith(initialState)
    }

    @Test fun `tapping on + increments the counter by +1`() {
      // when
      increment()

      // then
      val one = initialState.add(1)
      mviTestDelegate.assertStates(one)
    }

    @Test fun `tapping on - decrements the counter by -1`() {
      // when
      decrement()

      // then
      val minusOne = initialState.add(-1)
      mviTestDelegate.assertStates(minusOne)
    }
  }

  private fun increment() {
    intentions.onNext(Increment)
  }

  private fun decrement() {
    intentions.onNext(Decrement)
  }
}
