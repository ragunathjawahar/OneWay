package io.redgreen.oneway.catalogue.bmi.usecases

import io.reactivex.Observable
import io.redgreen.oneway.catalogue.bmi.BmiState
import io.redgreen.oneway.usecases.SourceCreatedUseCase
import io.redgreen.oneway.usecases.SourceRestoredUseCase

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
