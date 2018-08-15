package io.redgreen.oneway.catalogue.signup

sealed class SignUpIntention

data class EnterPhoneNumberIntention(
    val phoneNumber: String
) : SignUpIntention()

data class EnterUsernameIntention(
    val username: String
) : SignUpIntention()
