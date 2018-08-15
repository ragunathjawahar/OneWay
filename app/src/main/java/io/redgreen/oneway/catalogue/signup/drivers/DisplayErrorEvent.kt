package io.redgreen.oneway.catalogue.signup.drivers

sealed class DisplayErrorEvent

data class DisplayPhoneNumberErrorEvent(
    val displaying: Boolean = false
) : DisplayErrorEvent()
