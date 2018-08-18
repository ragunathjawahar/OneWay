package io.redgreen.oneway.catalogue.signup.form

import com.google.common.truth.Truth.assertThat
import io.redgreen.oneway.catalogue.signup.form.UsernameCondition.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

class UsernameInputTest {
  companion object {
    @JvmStatic fun invalidUsernames(): List<Arguments> {
      return listOf(
          Arguments.of("", setOf(ALLOWED_CHARACTERS, MIN_LENGTH, STARTS_WITH_ALPHABET)),
          Arguments.of(" ", values().toSet()),
          Arguments.of("a", setOf(MIN_LENGTH)),
          Arguments.of(" ab", setOf(ALLOWED_CHARACTERS, NO_SPACES, STARTS_WITH_ALPHABET)),
          Arguments.of("94949", setOf(STARTS_WITH_ALPHABET)),
          Arguments.of("what$", setOf(ALLOWED_CHARACTERS)),
          Arguments.of("@*#&2", setOf(ALLOWED_CHARACTERS, STARTS_WITH_ALPHABET))
      )
    }
  }

  private val validator = Validator()

  @ValueSource(strings = ["tom", "jackreacher", "wisdom99", "a1947", "voodoo513499"])
  @ParameterizedTest fun testValidUsername(username: String) {
    // when
    val unmetConditions = validator.validate<UsernameCondition>(username)

    // then
    assertThat(unmetConditions)
        .isEmpty()
  }

  @MethodSource("invalidUsernames")
  @ParameterizedTest fun testInvalidUsername(
      username: String,
      unmetConditions: Set<UsernameCondition>
  ) {
    // when
    val result = validator.validate<UsernameCondition>(username)

    // then
    assertThat(result)
        .containsExactlyElementsIn(unmetConditions)
  }
}
