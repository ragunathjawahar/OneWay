package io.redgreen.oneway.catalogue.signup.form

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class ValidUsernameTest(
    private val username: String
) {
  companion object {
    @Parameters @JvmStatic fun validUsernames(): List<String> =
        listOf("tom", "jackreacher", "wisdom99", "a1947", "voodoo513499")
  }

  private val validator = Validator()

  @Test fun `username is valid`() {
    // when
    val unmetConditions = validator.validate<UsernameCondition>(username)

    // then
    assertThat(unmetConditions)
        .isEmpty()
  }
}
