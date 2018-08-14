package io.mobsgeeks.oneway.catalogue.signup.form

import com.google.common.truth.Truth
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class ValidPhoneNumberTest(
    private val phoneNumber: String
) {
  companion object {
    @Parameters @JvmStatic fun validPhoneNumbers(): List<String> =
        listOf("9876543210", "8765432109")
  }

  private val validator = Validator()

  @Test
  fun `phone number is valid`() {
    // when
    val unmetConditions = validator.validate<PhoneNumberCondition>(phoneNumber)

    // then
    Truth.assertThat(unmetConditions)
        .isEmpty()
  }
}
