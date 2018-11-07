package io.redgreen.oneway.catalogue.budapest

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class BudapestState : Parcelable

@Parcelize object StrangerState : BudapestState() {
  fun blankName(): StrangerState =
      StrangerState

  fun onEnterName(intention: EnterNameIntention): GreeterState =
      GreeterState(intention.name)
}

@Parcelize data class GreeterState(val name: String) : BudapestState() {
  fun blankName(): StrangerState =
      StrangerState

  fun onEnterName(intention: EnterNameIntention): GreeterState =
      copy(name = intention.name)
}
