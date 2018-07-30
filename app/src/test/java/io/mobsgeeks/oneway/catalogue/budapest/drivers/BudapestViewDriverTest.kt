package io.mobsgeeks.oneway.catalogue.budapest.drivers

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import io.mobsgeeks.oneway.catalogue.budapest.BudapestState
import io.mobsgeeks.oneway.catalogue.budapest.BudapestView
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class BudapestViewDriverTest {
  private val statesSubject = PublishSubject.create<BudapestState>()

  @Test fun `it renders stranger state`() {
    // given
    val view = mock<BudapestView>()
    val viewDriver = BudapestViewDriver(view)
    val disposable = viewDriver.render(statesSubject)

    // when
    statesSubject.onNext(BudapestState.STRANGER)

    // then
    verify(view).greetStranger()
    verifyNoMoreInteractions(view)
    disposable.dispose()
  }
}
