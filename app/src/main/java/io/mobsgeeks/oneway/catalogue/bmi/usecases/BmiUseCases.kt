package io.mobsgeeks.oneway.catalogue.bmi.usecases

import io.mobsgeeks.oneway.catalogue.bmi.BmiState
import io.mobsgeeks.oneway.usecases.DefaultSourceCreatedUseCase
import io.mobsgeeks.oneway.usecases.DefaultSourceRestoredUseCase
import io.reactivex.Observable

class BmiUseCases(
    initialState: BmiState,
    val timeline: Observable<BmiState>
) {
  val sourceCreatedUseCase = DefaultSourceCreatedUseCase(initialState)
  val sourceRestoredUseCase = DefaultSourceRestoredUseCase(timeline)
  val changeWeightUseCase = ChangeWeightUseCase(timeline)
  val changeHeightUseCase = ChangeHeightUseCase(timeline)
  val changeMeasurementSystemUseCase = ChangeMeasurementSystemUseCase(timeline)
}
