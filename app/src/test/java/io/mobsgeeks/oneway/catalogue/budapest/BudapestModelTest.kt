package io.mobsgeeks.oneway.catalogue.budapest

import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.catalogue.budapest.usecases.BudapestUseCases
import io.mobsgeeks.oneway.catalogue.budapest.usecases.NameChangeUseCase
import io.mobsgeeks.oneway.test.MviTestRule
import io.mobsgeeks.oneway.usecases.DefaultSourceCreatedUseCase
import io.mobsgeeks.oneway.usecases.DefaultSourceRestoredUseCase
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class BudapestModelTest {
  private val intentions = PublishSubject.create<BudapestIntention>()

  private val sourceFunction = { sourceEvents: Observable<SourceEvent>, timeline: Observable<BudapestState> ->
    val useCases = BudapestUseCases(
        DefaultSourceCreatedUseCase(BudapestState.STRANGER),
        DefaultSourceRestoredUseCase(timeline),
        NameChangeUseCase()
    )
    BudapestModel.bind(intentions, sourceEvents, useCases)
  }

  private val testRule = MviTestRule(sourceFunction)

  @Test fun `creating a screen starts with a stranger state`() {
    // when
    testRule.screenIsCreated()

    // then
    testRule.assertStates(BudapestState.STRANGER)
  }

  @Test fun `restoring the screen restores the last known state`() {
    // when
    val spiderManState = BudapestState("Spider-Man")
    testRule.startWith(spiderManState) {
      testRule.screenIsRestored()
    }

    // then
    testRule.assertStates(spiderManState)
  }

  @Test fun `entering a name emits a greeter state`() {
    // given
    val name = "Goundamani"

    // when
    testRule.startWith(BudapestState.STRANGER) {
      intentions.onNext(NameChangeIntention(name))
    }

    // then
    testRule.assertStates(BudapestState(name))
  }
}
