package io.mobsgeeks.oneway.catalogue.budapest

import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.catalogue.budapest.usecases.BudapestUseCases
import io.mobsgeeks.oneway.test.MviTestRule
import io.mobsgeeks.oneway.usecases.DefaultBindingCreatedUseCase
import io.reactivex.Observable
import org.junit.Test

class BudapestModelTest {
  @Test fun `creating a screen starts with a stranger state`() {
    // given
    val useCases = BudapestUseCases(DefaultBindingCreatedUseCase(BudapestState.STRANGER))
    val sourceFunction = { bindings: Observable<Binding>, timeline: Observable<BudapestState> ->
      BudapestModel.bind(bindings, useCases)
    }
    val testRule = MviTestRule(sourceFunction)

    // when
    testRule.screenIsCreated()

    // then
    testRule.assertStates(BudapestState.STRANGER)
  }
}
