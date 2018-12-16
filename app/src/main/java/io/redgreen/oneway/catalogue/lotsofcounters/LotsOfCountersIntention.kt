package io.redgreen.oneway.catalogue.lotsofcounters

sealed class LotsOfCountersIntention

object AddCounterIntention : LotsOfCountersIntention()

data class IncrementCounterAtIntention(
    val index: Int
) : LotsOfCountersIntention()

data class DecrementCounterAtIntention(
    val index: Int
) : LotsOfCountersIntention()

data class RemoveCounterAtIntention(
    val index: Int
) : LotsOfCountersIntention()
