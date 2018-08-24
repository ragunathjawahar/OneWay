package io.redgreen.oneway.catalogue.budapest

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.SourceEvent
import io.redgreen.oneway.catalogue.budapest.BudapestState.Companion.STRANGER
import io.redgreen.oneway.catalogue.budapest.usecases.BudapestUseCases
import io.redgreen.oneway.test.MviTestRule
import org.junit.jupiter.api.Test

class BudapestModelTest {
  private val intentions = PublishSubject.create<BudapestIntention>()

  private val sourceFunction = { sourceEvents: Observable<SourceEvent>, timeline: Observable<BudapestState> ->
    BudapestModel.createSource(
        intentions,
        sourceEvents,
        BudapestUseCases(STRANGER, timeline)
    )
  }

  private val testRule = MviTestRule(sourceFunction)

  @Test fun `creating a screen starts with a stranger state`() {
    // when
    testRule.sourceIsCreated()

    // then
    testRule.assertStates(STRANGER)
  }

  @Test fun `restoring the screen restores the last known state`() {
    // when
    val spiderManState = BudapestState("Spider-Man")
    testRule.startWith(spiderManState) {
      testRule.sourceIsRestored()
    }

    // then
    testRule.assertStates(spiderManState)
  }

  @Test fun `entering a name emits a greeter state`() {
    // given
    val name = "Goundamani"

    // when
    testRule.startWith(STRANGER) {
      enterName(name)
    }

    // then
    testRule.assertStates(BudapestState(name))
  }

  private fun enterName(name: String) {
    intentions.onNext(EnterNameIntention(name))
  }
}
