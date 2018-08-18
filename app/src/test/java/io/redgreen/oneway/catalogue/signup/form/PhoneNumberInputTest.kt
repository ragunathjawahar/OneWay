package io.redgreen.oneway.catalogue.signup.form

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import io.redgreen.oneway.catalogue.signup.form.PhoneNumberCondition.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

class PhoneNumberInputTest {
  companion object {
    @JvmStatic fun invalidPhoneNumbers(): List<Arguments> {
      return listOf(
          Arguments.of("", PhoneNumberCondition.values().toSet()),
          Arguments.of("9", setOf(LENGTH)),
          Arguments.of("8", setOf(LENGTH)),
          Arguments.of("987654321", setOf(LENGTH)),
          Arguments.of("98765432101", setOf(LENGTH)),
          Arguments.of("5", setOf(LENGTH, STARTS_WITH)),
          Arguments.of("&*&8a", PhoneNumberCondition.values().toSet()),
          Arguments.of("abcdefghij", setOf(STARTS_WITH, DIGITS_ONLY))
      )
    }
  }

  private val validator = Validator()

  @ValueSource(strings = ["9876543210", "8765432109"])
  @ParameterizedTest fun testValidPhoneNumber(
      phoneNumber: String
  ) {
    // when
    val unmetConditions = validator.validate<PhoneNumberCondition>(phoneNumber)

    // then
    Truth.assertThat(unmetConditions)
        .isEmpty()
  }

  @MethodSource("invalidPhoneNumbers")
  @ParameterizedTest fun testInvalidPhoneNumber(
      phoneNumber: String,
      unmetConditions: Set<PhoneNumberCondition>
  ) {
    // when
    val result = validator.validate<PhoneNumberCondition>(phoneNumber)

    // then
    assertThat(result)
        .containsExactlyElementsIn(unmetConditions)
  }
}
