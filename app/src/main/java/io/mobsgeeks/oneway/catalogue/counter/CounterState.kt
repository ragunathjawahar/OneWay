package io.mobsgeeks.oneway.catalogue.counter

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize data class CounterState(val counter: Int) : Parcelable {
  companion object {
    val ZERO = CounterState(0)
  }

  fun add(number: Int): CounterState =
      copy(counter = counter + number)
}
