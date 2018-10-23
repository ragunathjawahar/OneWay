package io.redgreen.oneway.catalogue.smiley

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.catalogue.smiley.drivers.SmileyTransientViewDriver
import io.redgreen.oneway.catalogue.smiley.usecases.SmileyUseCases
import io.redgreen.oneway.test.MviTestHelper
import org.junit.jupiter.api.Test

class SmileyModelTest {
  private val intentions = PublishSubject.create<SmileyIntention>()

  private val initialSmileyState = SmileyState.initial("SMILE")

  private val transientViewDriver = mock<SmileyTransientViewDriver>()

  private val testHelper = MviTestHelper<SmileyState> { sourceLifecycleEvents, sourceCopy ->
    SmileyModel.createSource(
        intentions,
        sourceLifecycleEvents,
        SmileyUseCases(initialSmileyState, sourceCopy, transientViewDriver)
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
        .updateSmiley("GRUMPY")
    testHelper.setState(grumpySmileyState)

    // when
    testHelper.sourceIsDestroyed()
    testHelper.sourceIsRestored()

    // then
    testHelper.assertStates(grumpySmileyState)
  }

  @Test fun `when a smiley is picked, then show the CHOSEN ONE (make it feel special)`() {
    // given
    testHelper.setState(initialSmileyState)

    // when
    val winkSmiley = "WINK"
    intentions.onNext(PickSmileyIntention.of(winkSmiley))

    // then
    val winkSmileyState = initialSmileyState.updateSmiley(winkSmiley)
    testHelper.assertStates(winkSmileyState)
  }

  @Test fun `when the user does not pick a smiley, then notify the user about it`() {
    // given
    testHelper.setState(initialSmileyState)

    // when
    intentions.onNext(PickSmileyIntention.NONE)

    // then
    testHelper.assertNoStates()

    verify(transientViewDriver).pickSmileyCancelled()
    verifyNoMoreInteractions(transientViewDriver)
  }
}
