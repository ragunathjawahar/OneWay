package io.redgreen.oneway.catalogue.smiley

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize data class Smiley(
    val character: String
) : Parcelable
