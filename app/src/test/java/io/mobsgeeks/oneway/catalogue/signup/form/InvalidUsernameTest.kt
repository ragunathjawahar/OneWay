package io.mobsgeeks.oneway.catalogue.signup.form

import com.google.common.truth.Truth.assertThat
import io.mobsgeeks.oneway.catalogue.signup.form.Username.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class InvalidUsernameTest(
    usernameUnmetConditionsPair: Pair<String, Set<Username>>
) {
  private val username: String = usernameUnmetConditionsPair.first
  private val unmetConditions: Set<Username> = usernameUnmetConditionsPair.second

  companion object {
    @Parameters @JvmStatic fun invalidUsernames(): List<Pair<String, Set<Username>>> {
      return listOf(
          "" to setOf(ALLOWED_CHARACTERS, MIN_LENGTH, STARTS_WITH_ALPHABET),
          " " to Username.values().toSet(),
          "a" to setOf(MIN_LENGTH),
          " ab" to setOf(ALLOWED_CHARACTERS, NO_SPACES, STARTS_WITH_ALPHABET),
          "94949" to setOf(STARTS_WITH_ALPHABET),
          "what$" to setOf(ALLOWED_CHARACTERS),
          "@*#&2" to setOf(ALLOWED_CHARACTERS, STARTS_WITH_ALPHABET)
      )
    }
  }

  private val validator = Validator(*values())

  @Test fun `username is invalid`() {
    // when
    val result = validator.validate(username)

    // then
    assertThat(result)
        .containsExactlyElementsIn(unmetConditions)
  }
}
