package io.mobsgeeks.oneway.catalogue.signup

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

  private val validator = Validator(*Username.values())

  @Test fun `username is valid`() {
    // when
    val unmetConditions = validator.validate(username)

    // then
    assertThat(unmetConditions)
        .isEmpty()
  }
}
