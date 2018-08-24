package io.redgreen.oneway.catalogue.counter.drivers

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.catalogue.counter.CounterState
import io.redgreen.oneway.catalogue.counter.CounterView
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class CounterViewDriverTest {
  private val statesSubject = PublishSubject.create<CounterState>()
  private val view = mock<CounterView>()
  private val viewDriver = CounterViewDriver(view)
  private val disposable = viewDriver.render(statesSubject)

  @AfterEach fun teardown() {
    disposable.dispose()
  }

  @Test fun `it renders counter value`() {
    // given
    val seven = CounterState(7)

    // when
    statesSubject.onNext(seven)

    // then
    verify(view).showCounter(seven.counter)
    verifyNoMoreInteractions(view)
  }
}
