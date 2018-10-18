package io.redgreen.oneway.catalogue.smiley

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize data class SmileyState(val smiley: String) : Parcelable {
  companion object {
    fun initial(smiley: String): SmileyState =
        SmileyState(smiley)
  }

  fun updateSmiley(smiley: String): SmileyState =
      copy(smiley = smiley)
}
