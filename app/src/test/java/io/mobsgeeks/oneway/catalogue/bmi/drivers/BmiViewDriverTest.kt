package io.mobsgeeks.oneway.catalogue.bmi.drivers

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import io.mobsgeeks.oneway.catalogue.bmi.BmiState
import io.mobsgeeks.oneway.catalogue.bmi.BmiView
import io.mobsgeeks.oneway.catalogue.bmi.domain.MeasurementSystem.SI
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class BmiViewDriverTest {
  @Test fun `it should render bmi, category, weight, height and measurement system`() {
    // given
    val statesSubject = PublishSubject.create<BmiState>()
    val view = mock<BmiView>()
    val viewDriver = BmiViewDriver(view)
    val disposable = viewDriver.render(statesSubject)

    // when
    val bmiState = BmiState(48.0, 160.0, SI)
    statesSubject.onNext(bmiState)

    // then
    verify(view).showBmi(bmiState.bmi)
    verify(view).showCategory(bmiState.category)
    verify(view).showWeight(bmiState.weight, bmiState.measurementSystem)
    verify(view).showHeight(bmiState.height, bmiState.measurementSystem)
    verify(view).showMeasurementSystem(bmiState.measurementSystem)
    verifyNoMoreInteractions(view)

    disposable.dispose()
  }
}
