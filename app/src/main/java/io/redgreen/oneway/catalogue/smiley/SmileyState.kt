package io.redgreen.oneway.catalogue.smiley

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize data class SmileyState(val smiley: Smiley) : Parcelable {
  companion object {
    fun initial(character: String): SmileyState =
        SmileyState(Smiley(character))
  }

  fun updateSmiley(character: String): SmileyState =
      copy(smiley = Smiley(character))
}
