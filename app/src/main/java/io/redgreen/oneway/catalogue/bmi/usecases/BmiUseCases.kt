package io.redgreen.oneway.catalogue.bmi.usecases

import io.reactivex.Observable
import io.redgreen.oneway.catalogue.bmi.BmiState
import io.redgreen.oneway.usecases.SourceCreatedUseCase
import io.redgreen.oneway.usecases.SourceRestoredUseCase

class BmiUseCases(
    initialState: BmiState,
    val sourceCopy: Observable<BmiState>
) {
  val sourceCreatedUseCase = SourceCreatedUseCase(initialState)
  val sourceRestoredUseCase = SourceRestoredUseCase(sourceCopy)
  val changeWeightUseCase = ChangeWeightUseCase(sourceCopy)
  val changeHeightUseCase = ChangeHeightUseCase(sourceCopy)
  val changeMeasurementSystemUseCase = ChangeMeasurementSystemUseCase(sourceCopy)
}
