package io.redgreen.oneway.catalogue.budapest

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.SourceEvent
import io.redgreen.oneway.catalogue.budapest.BudapestState.Companion.STRANGER
import io.redgreen.oneway.catalogue.budapest.usecases.BudapestUseCases
import io.redgreen.oneway.test.MviTestDelegate
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

  private val testDelegate = MviTestDelegate(sourceFunction)

  @Test fun `creating a screen starts with a stranger state`() {
    // when
    testDelegate.sourceIsCreated()

    // then
    testDelegate.assertStates(STRANGER)
  }

  @Test fun `restoring the screen restores the last known state`() {
    // when
    val spiderManState = BudapestState("Spider-Man")
    testDelegate.setState(spiderManState) {
      testDelegate.sourceIsRestored()
    }

    // then
    testDelegate.assertStates(spiderManState)
  }

  @Test fun `entering a name emits a greeter state`() {
    // given
    val name = "Goundamani"

    // when
    testDelegate.setState(STRANGER) {
      enterName(name)
    }

    // then
    testDelegate.assertStates(BudapestState(name))
  }

  private fun enterName(name: String) {
    intentions.onNext(EnterNameIntention(name))
  }
}
