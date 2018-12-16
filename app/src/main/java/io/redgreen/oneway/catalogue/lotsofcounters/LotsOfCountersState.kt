package io.redgreen.oneway.catalogue.lotsofcounters

import arrow.data.ListK
import arrow.data.k
import arrow.optics.dsl.index
import arrow.optics.instances.listk.index.index
import arrow.optics.optics
import io.redgreen.oneway.catalogue.counter.CounterState

@optics data class LotsOfCountersState(
    val counterStates: ListK<CounterState>
) {
  companion object {
    val NO_COUNTERS = LotsOfCountersState(listOf<CounterState>().k())
  }

  private val counterStatesLens = LotsOfCountersState.counterStates

  fun addCounter(): LotsOfCountersState {
    return counterStatesLens.modify(this) { counterStates ->
      counterStates
          .toMutableList()
          .apply { add(CounterState.ZERO) }
          .k()
    }
  }

  fun incrementCounter(counterToIncrement: Int): LotsOfCountersState =
      updateCounter(counterToIncrement, 1)

  fun decrementCounter(counterToDecrement: Int): LotsOfCountersState =
      updateCounter(counterToDecrement, -1)

  private fun updateCounter(
      index: Int,
      numberToAdd: Int
  ): LotsOfCountersState {
    val counterStateOptional = counterStatesLens.index(ListK.index(), index)
    return counterStateOptional.modify(this) { counterState ->
      counterState.add(numberToAdd)
    }
  }

  fun removeCounter(index: Int): LotsOfCountersState {
    return counterStatesLens.modify(this) { counterStates ->
      counterStates
          .toMutableList()
          .apply { removeAt(index) }
          .k()
    }
  }
}
