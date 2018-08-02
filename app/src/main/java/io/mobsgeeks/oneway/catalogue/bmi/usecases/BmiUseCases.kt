package io.mobsgeeks.oneway.catalogue.bmi.usecases

import io.mobsgeeks.oneway.catalogue.bmi.BmiState
import io.mobsgeeks.oneway.usecases.DefaultSourceCreatedUseCase
import io.mobsgeeks.oneway.usecases.DefaultSourceRestoredUseCase

class BmiUseCases(
    val createdUseCase: DefaultSourceCreatedUseCase<BmiState>,
    val restoredUseCase: DefaultSourceRestoredUseCase<BmiState>,
    val changeWeightUseCase: ChangeWeightUseCase,
    val changeHeightUseCase: ChangeHeightUseCase,
    val changeMeasurementSystemUseCase: ChangeMeasurementSystemUseCase
)
