package io.redgreen.oneway.catalogue.signup.drivers

import io.redgreen.oneway.catalogue.signup.form.WhichField

data class DisplayErrorEvent(
    val whichField: WhichField,
    val display: Boolean = false
)
