package io.mobsgeeks.oneway.catalogue.signup

sealed class SignUpIntention

data class EnterPhoneNumberIntention(
    val phoneNumber: String
) : SignUpIntention()
