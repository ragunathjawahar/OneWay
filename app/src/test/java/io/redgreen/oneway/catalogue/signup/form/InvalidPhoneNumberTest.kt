package io.redgreen.oneway.catalogue.signup.form

import com.google.common.truth.Truth.assertThat
import io.redgreen.oneway.catalogue.signup.form.PhoneNumberCondition.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class InvalidPhoneNumberTest(
    phoneNumberUnmetConditionsPair: Pair<String, Set<PhoneNumberCondition>>
) {
  companion object {
    @Parameters @JvmStatic fun invalidUsernames(): List<Pair<String, Set<PhoneNumberCondition>>> {
      return listOf(
          "" to PhoneNumberCondition.values().toSet(),
          "9" to setOf(LENGTH),
          "8" to setOf(LENGTH),
          "987654321" to setOf(LENGTH),
          "98765432101" to setOf(LENGTH),
          "5" to setOf(LENGTH, STARTS_WITH),
          "&*&8a" to PhoneNumberCondition.values().toSet(),
          "abcdefghij" to setOf(STARTS_WITH, DIGITS_ONLY)
      )
    }
  }

  private val phoneNumber: String = phoneNumberUnmetConditionsPair.first
  private val unmetConditions: Set<PhoneNumberCondition> = phoneNumberUnmetConditionsPair.second
  private val validator = Validator()

  @Test fun `phone number is invalid`() {
    // when
    val result = validator.validate<PhoneNumberCondition>(phoneNumber)

    // then
    assertThat(result)
        .containsExactlyElementsIn(unmetConditions)
  }
}
