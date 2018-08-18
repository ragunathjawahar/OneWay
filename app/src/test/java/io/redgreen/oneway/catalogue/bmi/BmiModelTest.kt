package io.redgreen.oneway.catalogue.bmi

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.SourceEvent
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem.IMPERIAL
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem.SI
import io.redgreen.oneway.catalogue.bmi.usecases.BmiUseCases
import io.redgreen.oneway.test.MviTestRule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BmiModelTest {
  private val initialState = BmiState(48.0, 160.0, SI)
  private val intentions = PublishSubject.create<BmiIntention>()
  private val testRule = MviTestRule { sourceEvents: Observable<SourceEvent>, timeline: Observable<BmiState> ->
    BmiModel.createSource(
        intentions,
        sourceEvents,
        BmiUseCases(initialState, timeline)
    )
  }

  @Test fun `when source is created, then show initial state`() {
    // when
    testRule.sourceIsCreated()

    // then
    testRule.assertStates(initialState)
  }

  @Test fun `when source is restored, then show last known state`() {
    // given
    val lastKnownState = BmiState(70.0, 170.0, SI)

    // when
    testRule.startWith(lastKnownState) {
      testRule.sourceIsDestroyed()
      testRule.sourceIsRestored()
    }

    // then
    testRule.assertStates(lastKnownState)
  }

  @DisplayName("after source is created")
  @Nested inner class AfterSourceIsCreated {
    @BeforeEach fun setup() {
      testRule.startWith(initialState)
    }

    @Test fun `when user changes weight, then update weight`() {
      // given
      val updatedWeightInKg = 72.0

      // when
      changeWeight(updatedWeightInKg)

      // then
      val updatedWeightState = initialState.updateWeight(updatedWeightInKg)
      testRule.assertStates(updatedWeightState)
    }

    @Test fun `when user changes height, then update height`() {
      // given
      val updatedHeightInCm = 180.0

      // when
      changeHeight(updatedHeightInCm)

      // then
      val updatedHeightState = initialState.updateHeight(updatedHeightInCm)
      testRule.assertStates(updatedHeightState)
    }

    @Test fun `when user changes measurement system, then update measurement system`() {
      // given
      val updatedMeasurementSystem = IMPERIAL

      // when
      changeMeasurementSystem(updatedMeasurementSystem)

      // then
      val updatedMeasurementSystemState = initialState.updateMeasurementSystem(updatedMeasurementSystem)
      testRule.assertStates(updatedMeasurementSystemState)
    }
  }

  private fun changeWeight(weightInKg: Double) {
    intentions.onNext(ChangeWeightIntention(weightInKg))
  }

  private fun changeHeight(heightInCm: Double) {
    intentions.onNext(ChangeHeightIntention(heightInCm))
  }

  private fun changeMeasurementSystem(measurementSystem: MeasurementSystem) {
    intentions.onNext(ChangeMeasurementSystemIntention(measurementSystem))
  }
}
