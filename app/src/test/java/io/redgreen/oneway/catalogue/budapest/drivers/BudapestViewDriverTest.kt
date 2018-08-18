package io.redgreen.oneway.catalogue.budapest.drivers

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.catalogue.budapest.BudapestState
import io.redgreen.oneway.catalogue.budapest.BudapestView
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class BudapestViewDriverTest {
  private val statesSubject = PublishSubject.create<BudapestState>()
  private val view = mock<BudapestView>()
  private val viewDriver = BudapestViewDriver(view)
  private val disposable = viewDriver.render(statesSubject)

  @AfterEach fun teardown() {
    disposable.dispose()
  }

  @Test fun `it renders stranger state`() {
    // when
    statesSubject.onNext(BudapestState.STRANGER)

    // then
    verify(view).greetStranger()
    verifyNoMoreInteractions(view)
  }

  @Test fun `it renders greeter state`() {
    // given
    val hulk = "Hulk"
    val greeterState = BudapestState(hulk)

    // when
    statesSubject.onNext(greeterState)

    // then
    verify(view).greet(hulk)
    verifyNoMoreInteractions(view)
  }
}
