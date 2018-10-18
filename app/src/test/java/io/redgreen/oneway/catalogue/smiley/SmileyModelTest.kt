package io.redgreen.oneway.catalogue.smiley

import io.redgreen.oneway.test.MviTestHelper
import io.redgreen.oneway.usecases.SourceCreatedUseCase
import org.junit.jupiter.api.Test

class SmileyModelTest {
  @Test fun `when screen is created, then show the default (smiling) smiley`() {
    // given
    val smileSmileyState = SmileyState.initial("SMILE")
    val testHelper = MviTestHelper<SmileyState> { sourceLifecycleEvents, _ ->
      SmileyModel.createSource(sourceLifecycleEvents, SourceCreatedUseCase(smileSmileyState))
    }

    // when
    testHelper.sourceIsCreated()

    // then
    testHelper.assertStates(smileSmileyState)
  }
}
