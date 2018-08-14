package io.mobsgeeks.oneway.catalogue.signup.form

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UsernameConditionsTest {
  @Test fun `username cannot be empty`() {
    // given
    val username = ""

    // when
    val valid = UsernameCondition.MIN_LENGTH.isValid(username)

    // then
    assertThat(valid)
        .isFalse()
  }

  @Test fun `username should have 3 or more characters`() {
    // given
    val username = "tom"

    // when
    val valid = UsernameCondition.MIN_LENGTH.isValid(username)

    // then
    assertThat(valid)
        .isTrue()
  }

  @Test fun `username cannot contain spaces`() {
    // given
    val justSpaces = "   "

    // when
    val valid = UsernameCondition.NO_SPACES.isValid(justSpaces)

    // then
    assertThat(valid)
        .isFalse()
  }

  @Test fun `username can contain alphabets`() {
    // given
    val username = "ethan"

    // when
    val valid = UsernameCondition.ALLOWED_CHARACTERS.isValid(username)

    // then
    assertThat(valid)
        .isTrue()
  }

  @Test fun `username can contain numbers`() {
    // given
    val username = "ethan92"

    // when
    val valid = UsernameCondition.ALLOWED_CHARACTERS.isValid(username)

    // then
    assertThat(valid)
        .isTrue()
  }

  @Test fun `username cannot contain special characters`() {
    // given
    val username = "ethan@92"

    // when
    val valid = UsernameCondition.ALLOWED_CHARACTERS.isValid(username)

    // then
    assertThat(valid)
        .isFalse()
  }

  @Test fun `username should start with an alphabet`() {
    // given
    val username = "8neo"

    // when
    val valid = UsernameCondition.STARTS_WITH_ALPHABET.isValid(username)

    // then
    assertThat(valid)
        .isFalse()
  }
}
