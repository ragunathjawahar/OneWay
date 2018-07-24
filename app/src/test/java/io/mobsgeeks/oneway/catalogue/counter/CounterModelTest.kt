package io.mobsgeeks.oneway.catalogue.counter

import io.mobsgeeks.oneway.catalogue.counter.CounterState.Companion.ZERO
import io.mobsgeeks.oneway.catalogue.counter.usecases.*
import io.mobsgeeks.oneway.test.MviTestRule
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class CounterModelTest {
  private val intentions = PublishSubject.create<CounterIntention>()

  private val mviTestRule = MviTestRule<CounterState> { bindings, timeline ->
    val useCases = CounterUseCases(
        CreatedUseCase(),
        RestoredUseCase(timeline),
        IncrementUseCase(timeline),
        DecrementUseCase(timeline)
    )

    return@MviTestRule CounterModel.bind(intentions, bindings, useCases)
  }

  @Test fun `creating the screen starts with a ZERO state`() {
    // when
    mviTestRule.screenIsCreated()

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
      mviTestRule.screenIsRestored()
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
