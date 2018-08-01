package io.mobsgeeks.oneway.catalogue.bmi.usecases

import io.mobsgeeks.oneway.catalogue.bmi.BmiState
import io.mobsgeeks.oneway.usecases.DefaultBindingRestoredUseCase

class BmiUseCases(
    val restoredUseCase: DefaultBindingRestoredUseCase<BmiState>,
    val changeWeightUseCase: ChangeWeightUseCase,
    val changeHeightUseCase: ChangeHeightUseCase,
    val changeMeasurementSystemUseCase: ChangeMeasurementSystemUseCase
)
