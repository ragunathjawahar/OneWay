package io.mobsgeeks.oneway.catalogue.counter

sealed class CounterIntention
object Increment : CounterIntention()
object Decrement : CounterIntention()
