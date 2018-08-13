package io.mobsgeeks.oneway.catalogue.signup

import io.mobsgeeks.oneway.catalogue.signup.SignUpModel.createSource
import io.mobsgeeks.oneway.catalogue.signup.SignUpState.Companion.UNTOUCHED
import io.mobsgeeks.oneway.test.MviTestRule
import io.mobsgeeks.oneway.usecases.SourceCreatedUseCase
import org.junit.Test

class SignUpModelTest {
  @Test fun `creating the screen starts with an untouched state`() {
    // given
    val testRule = MviTestRule<SignUpState> { sourceEvents, timeline ->
      createSource(
          sourceEvents,
          SourceCreatedUseCase(UNTOUCHED)
      )
    }

    // when
    testRule.sourceIsCreated()

    // then
    testRule.assertStates(UNTOUCHED)
  }
}
