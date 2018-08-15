package io.redgreen.oneway.catalogue.counter

sealed class CounterIntention
object Increment : CounterIntention()
object Decrement : CounterIntention()
