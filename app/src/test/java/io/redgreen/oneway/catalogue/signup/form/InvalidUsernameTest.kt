package io.redgreen.oneway.catalogue.signup.form

import com.google.common.truth.Truth.assertThat
import io.redgreen.oneway.catalogue.signup.form.UsernameCondition.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class InvalidUsernameTest(
    usernameUnmetConditionsPair: Pair<String, Set<UsernameCondition>>
) {
  companion object {
    @Parameters @JvmStatic fun invalidUsernames(): List<Pair<String, Set<UsernameCondition>>> {
      return listOf(
          "" to setOf(ALLOWED_CHARACTERS, MIN_LENGTH, STARTS_WITH_ALPHABET),
          " " to UsernameCondition.values().toSet(),
          "a" to setOf(MIN_LENGTH),
          " ab" to setOf(ALLOWED_CHARACTERS, NO_SPACES, STARTS_WITH_ALPHABET),
          "94949" to setOf(STARTS_WITH_ALPHABET),
          "what$" to setOf(ALLOWED_CHARACTERS),
          "@*#&2" to setOf(ALLOWED_CHARACTERS, STARTS_WITH_ALPHABET)
      )
    }
  }

  private val username: String = usernameUnmetConditionsPair.first
  private val unmetConditions: Set<UsernameCondition> = usernameUnmetConditionsPair.second
  private val validator = Validator()

  @Test fun `username is invalid`() {
    // when
    val result = validator.validate<UsernameCondition>(username)

    // then
    assertThat(result)
        .containsExactlyElementsIn(unmetConditions)
  }
}
