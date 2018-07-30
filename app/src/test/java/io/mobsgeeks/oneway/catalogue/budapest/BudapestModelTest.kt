package io.mobsgeeks.oneway.catalogue.budapest

import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.catalogue.budapest.usecases.BudapestUseCases
import io.mobsgeeks.oneway.test.MviTestRule
import io.mobsgeeks.oneway.usecases.DefaultBindingCreatedUseCase
import io.mobsgeeks.oneway.usecases.DefaultBindingRestoredUseCase
import io.reactivex.Observable
import org.junit.Test

class BudapestModelTest {
  private val sourceFunction = { bindings: Observable<Binding>, timeline: Observable<BudapestState> ->
    val useCases = BudapestUseCases(
        DefaultBindingCreatedUseCase(BudapestState.STRANGER),
        DefaultBindingRestoredUseCase(timeline)
    )
    BudapestModel.bind(bindings, useCases)
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
}
