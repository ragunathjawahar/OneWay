package io.mobsgeeks.oneway.catalogue.bmi.usecases

import io.mobsgeeks.oneway.catalogue.bmi.BmiState
import io.mobsgeeks.oneway.usecases.SourceCreatedUseCase
import io.mobsgeeks.oneway.usecases.SourceRestoredUseCase
import io.reactivex.Observable

class BmiUseCases(
    initialState: BmiState,
    val timeline: Observable<BmiState>
) {
  val sourceCreatedUseCase = SourceCreatedUseCase(initialState)
  val sourceRestoredUseCase = SourceRestoredUseCase(timeline)
  val changeWeightUseCase = ChangeWeightUseCase(timeline)
  val changeHeightUseCase = ChangeHeightUseCase(timeline)
  val changeMeasurementSystemUseCase = ChangeMeasurementSystemUseCase(timeline)
}
