package io.redgreen.oneway.catalogue.counter.drivers

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.catalogue.counter.CounterState
import io.redgreen.oneway.catalogue.counter.CounterView
import org.junit.jupiter.api.Test

class CounterViewDriverTest {
  @Test fun `it renders counter value`() {
    // given
    val statesSubject = PublishSubject.create<CounterState>()
    val view = mock<CounterView>()
    val viewDriver = CounterViewDriver(view)
    val disposable = viewDriver.render(statesSubject)

    // when
    val seven = CounterState(7)
    statesSubject.onNext(seven)

    // then
    verify(view).showCounter(seven.counter)
    verifyNoMoreInteractions(view)
    disposable.dispose()
  }
}
