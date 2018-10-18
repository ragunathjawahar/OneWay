package io.redgreen.oneway.catalogue.smiley

import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.catalogue.smiley.usecases.SmileyUseCases
import io.redgreen.oneway.test.MviTestHelper
import org.junit.jupiter.api.Test

class SmileyModelTest {
  private val intentions = PublishSubject.create<SmileyIntention>()

  private val initialSmileyState = SmileyState.initial("SMILE")

  private val testHelper = MviTestHelper<SmileyState> { sourceLifecycleEvents, sourceCopy ->
    SmileyModel.createSource(
        intentions,
        sourceLifecycleEvents,
        SmileyUseCases(initialSmileyState, sourceCopy)
    )
  }

  @Test fun `when screen is created, then show the default (smiling) smiley`() {
    // when
    testHelper.sourceIsCreated()

    // then
    testHelper.assertStates(initialSmileyState)
  }

  @Test fun `when screen is restored, then show last known state`() {
    // given
    val grumpySmileyState = initialSmileyState
        .setSmiley("GRUMPY")
    testHelper.setState(grumpySmileyState)

    // when
    testHelper.sourceIsDestroyed()
    testHelper.sourceIsRestored()

    // then
    testHelper.assertStates(grumpySmileyState)
  }

  @Test fun `when a smiley is chosen, then show the CHOSEN ONE (make it feel special)`() {
    // given
    testHelper.setState(initialSmileyState)

    // when
    val winkSmiley = "WINK"
    intentions.onNext(ChooseSmileyIntention(winkSmiley))

    // then
    val winkSmileyState = initialSmileyState.setSmiley(winkSmiley)
    testHelper.assertStates(winkSmileyState)
  }
}
