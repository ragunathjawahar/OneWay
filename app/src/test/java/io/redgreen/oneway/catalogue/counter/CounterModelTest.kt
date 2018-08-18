package io.redgreen.oneway.catalogue.counter

import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.catalogue.counter.CounterState.Companion.ZERO
import io.redgreen.oneway.catalogue.counter.usecases.CounterUseCases
import io.redgreen.oneway.test.MviTestRule
import org.junit.jupiter.api.Test

class CounterModelTest {
  private val intentions = PublishSubject.create<CounterIntention>()

  private val mviTestRule = MviTestRule<CounterState> { sourceEvents, timeline ->
    return@MviTestRule CounterModel.createSource(
        intentions,
        sourceEvents,
        CounterUseCases(CounterState.ZERO, timeline)
    )
  }

  @Test fun `creating the screen starts with a ZERO state`() {
    // when
    mviTestRule.sourceIsCreated()

    // then
    mviTestRule.assertStates(ZERO)
  }

  @Test fun `tapping on + increments the counter by 1`() {
    // when
    mviTestRule.startWith(ZERO) {
      increment()
    }

    // then
    val one = ZERO.add(1)
    mviTestRule.assertStates(one)
  }

  @Test fun `tapping on - decrements the counter by 1`() {
    // when
    mviTestRule.startWith(ZERO) {
      decrement()
    }

    // then
    val minusOne = ZERO.add(-1)
    mviTestRule.assertStates(minusOne)
  }

  @Test fun `restoring the screen restores the previous state`() {
    // given
    val three = CounterState(3)

    // when
    mviTestRule.startWith(three) {
      mviTestRule.sourceIsRestored()
    }

    // then
    mviTestRule.assertStates(three)
  }

  private fun increment() {
    intentions.onNext(Increment)
  }

  private fun decrement() {
    intentions.onNext(Decrement)
  }
}
