package io.redgreen.oneway.catalogue.lotsofcounters

import arrow.data.k
import com.google.common.truth.Truth.assertThat
import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.catalogue.counter.CounterState
import io.redgreen.oneway.test.MviTestHelper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LotsOfCountersModelTest {
  private val testHelper = MviTestHelper<LotsOfCountersState>()
  private val intentions = PublishSubject.create<LotsOfCountersIntention>()

  @BeforeEach fun setup() {
    testHelper.setSource { sourceLifecycleEvents, sourceCopy ->
      LotsOfCountersModel.createSource(intentions, sourceLifecycleEvents, sourceCopy)
    }
  }

  @Test fun `when source is created, then emit no counters state`() {
    val initialState = LotsOfCountersState.NO_COUNTERS // []

    // when
    testHelper.sourceIsCreated()

    // then
    testHelper.assertStates(
        initialState // []
    )

    assertThat(initialState.counterStates).isEmpty()
  }

  @Test fun `when source is restored, then emit the last known state`() {
    // given
    val five = 5
    val lastKnownState = LotsOfCountersState(listOf(CounterState(five)).k()) // [5]
    testHelper.setState(lastKnownState)

    // when
    testHelper.sourceIsRestored()

    // then
    testHelper.assertStates(
        lastKnownState // [5]
    )

    assertThat(lastKnownState.counterStates).hasSize(1)
    assertThat(lastKnownState.counterStates[0].counter).isEqualTo(five)
  }

  @Test fun `when user clicks on add counter, a new counter is added`() {
    // given
    val noCountersState = LotsOfCountersState.NO_COUNTERS // []
    testHelper.setState(noCountersState)

    // when
    intentions.onNext(AddCounterIntention)

    // then
    val oneCounterState = noCountersState.addCounter()
    testHelper.assertStates(
        oneCounterState // [0]
    )

    assertThat(oneCounterState.counterStates).hasSize(1)
    assertThat(oneCounterState.counterStates[0].counter).isEqualTo(0)
  }

  @Test fun `when user clicks on increment counter, then increment counter value`() {
    // given
    val twoZeroCountersState = LotsOfCountersState.NO_COUNTERS
        .addCounter()
        .addCounter() // [0, 0]
    testHelper.setState(twoZeroCountersState)

    // when
    val counterToIncrement = 1
    intentions.onNext(IncrementCounterAtIntention(counterToIncrement))

    // then
    val secondCounterIncrementedState = twoZeroCountersState
        .incrementCounter(counterToIncrement)
    testHelper.assertStates(
        secondCounterIncrementedState // [0, 1]
    )

    // TODO(rj) 16/Dec/18 - Use `Eq` from Arrow?
    val counterStates = secondCounterIncrementedState.counterStates
    assertThat(counterStates).hasSize(2)
    assertThat(counterStates[0].counter).isEqualTo(0)
    assertThat(counterStates[1].counter).isEqualTo(1)
  }

  @Test fun `when user clicks on decrement counter, then decrement counter value`() {
    // given
    val twoZeroCountersState = LotsOfCountersState.NO_COUNTERS
        .addCounter()
        .addCounter() // [0, 0]
    testHelper.setState(twoZeroCountersState)

    // when
    val counterToDecrement = 0
    intentions.onNext(DecrementCounterAtIntention(counterToDecrement))

    // then
    val firstCounterDecrementedState = twoZeroCountersState
        .decrementCounter(counterToDecrement)
    testHelper.assertStates(
        firstCounterDecrementedState // [-1, 0]
    )

    // TODO(rj) 16/Dec/18 - Use `Eq` from Arrow?
    val counterStates = firstCounterDecrementedState.counterStates
    assertThat(counterStates).hasSize(2)
    assertThat(counterStates[0].counter).isEqualTo(-1)
    assertThat(counterStates[1].counter).isEqualTo(0)
  }

  @Test fun `when user clicks on remove counter, then remove that counter`() {
    // given
    val zeroCountersState = LotsOfCountersState.NO_COUNTERS
        .addCounter()
        .addCounter()
        .incrementCounter(0)
        .incrementCounter(0) // [2, 0]
    testHelper.setState(zeroCountersState)

    // when
    val counterToRemove = 0
    intentions.onNext(RemoveCounterAtIntention(counterToRemove))

    // then
    val firstCounterRemovedState = zeroCountersState
        .removeCounter(counterToRemove)
    testHelper.assertStates(
        firstCounterRemovedState // [0]
    )

    // TODO(rj) 16/Dec/18 - Use `Eq` from Arrow?
    val counterStates = firstCounterRemovedState.counterStates
    assertThat(counterStates).hasSize(1)
    assertThat(counterStates[0].counter).isEqualTo(0)
  }
}
