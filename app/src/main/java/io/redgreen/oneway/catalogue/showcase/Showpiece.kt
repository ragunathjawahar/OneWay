package io.redgreen.oneway.catalogue.showcase

import android.support.v4.app.Fragment

data class Showpiece(
    val title: String,
    val fragmentCreator: () -> Fragment
)
