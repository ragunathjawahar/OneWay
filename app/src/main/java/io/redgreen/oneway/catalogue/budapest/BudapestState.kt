package io.redgreen.oneway.catalogue.budapest

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize data class BudapestState(val name: String) : Parcelable {
  companion object {
    val STRANGER = BudapestState("")
  }
}
