package io.mobsgeeks.oneway.catalogue.signup.form

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PhoneNumberConditionsTest {
  @Test fun `phone number should have 10 digits`() {
    // given
    val phoneNumber = "9876543210"

    // when
    val valid = PhoneNumber.LENGTH.isValid(phoneNumber)

    // then
    assertThat(valid)
        .isTrue()
  }

  @Test fun `phone number should only have digits`() {
    // given
    val phoneNumber = "abc123*345"

    // when
    val valid = PhoneNumber.DIGITS_ONLY.isValid(phoneNumber)

    // then
    assertThat(valid)
        .isFalse()
  }

  @Test fun `phone numbers should start with a 9 or a 8`() {
    // given
    val phoneNumberStartingWith9 = "9876543210"
    val phoneNumberStartingWith8 = "8976543210"

    // when
    val numberStartingWith9IsValid = PhoneNumber.STARTS_WITH.isValid(phoneNumberStartingWith9)
    val numberStartingWith8IsValid = PhoneNumber.STARTS_WITH.isValid(phoneNumberStartingWith8)

    // then
    assertThat(numberStartingWith9IsValid)
        .isTrue()

    assertThat(numberStartingWith8IsValid)
        .isTrue()
  }

  @Test fun `phone numbers that do not start with 9 or 8 is invalid`() {
    // given
    val notPhoneNumber = "6123456789"

    // when
    val valid = PhoneNumber.STARTS_WITH.isValid(notPhoneNumber)

    assertThat(valid)
        .isFalse()
  }
}
