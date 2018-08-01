package io.mobsgeeks.oneway.catalogue.bmi

import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.catalogue.bmi.MeasurementSystem.SI
import io.mobsgeeks.oneway.catalogue.bmi.usecases.BmiUseCases
import io.mobsgeeks.oneway.catalogue.bmi.usecases.ChangeWeightUseCase
import io.mobsgeeks.oneway.test.MviTestRule
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class BmiModelTest {
  @Test fun `when user changes weight, then update weight`() {
    // given
    val intentions = PublishSubject.create<BmiIntention>()
    val testRule = MviTestRule { _: Observable<Binding>, timeline: Observable<BmiState> ->
      val useCases = BmiUseCases(ChangeWeightUseCase(timeline))
      BmiModel.bind(intentions, useCases)
    }

    // when
    val startState = BmiState(70.0, 170.0, SI)
    val updatedWeightInKg = 72.0
    testRule.startWith(startState) {
      intentions.onNext(ChangeWeightIntention(updatedWeightInKg))
    }

    // then
    val updatedWeightState = startState.copy(weightInKg = updatedWeightInKg)
    testRule.assertStates(updatedWeightState)
  }
}
