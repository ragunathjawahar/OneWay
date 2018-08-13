package io.mobsgeeks.oneway.catalogue.signup.form

import com.google.common.truth.Truth.assertThat
import io.mobsgeeks.oneway.catalogue.signup.form.PhoneNumber.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class InvalidPhoneNumberTest(
    phoneNumberUnmetConditionsPair: Pair<String, Set<PhoneNumber>>
) {
  private val phoneNumber: String = phoneNumberUnmetConditionsPair.first
  private val unmetConditions: Set<PhoneNumber> = phoneNumberUnmetConditionsPair.second

  companion object {
    @Parameters @JvmStatic fun invalidUsernames(): List<Pair<String, Set<PhoneNumber>>> {
      return listOf(
          "" to PhoneNumber.values().toSet(),
          "9" to setOf(LENGTH),
          "8" to setOf(LENGTH),
          "987654321" to setOf(LENGTH),
          "98765432101" to setOf(LENGTH),
          "5" to setOf(LENGTH, STARTS_WITH),
          "&*&8a" to PhoneNumber.values().toSet(),
          "abcdefghij" to setOf(STARTS_WITH, DIGITS_ONLY)
      )
    }
  }

  private val validator = Validator()

  @Test fun `phone number is invalid`() {
    // when
    val result = validator.validate<PhoneNumber>(phoneNumber)

    // then
    assertThat(result)
        .containsExactlyElementsIn(unmetConditions)
  }
}
