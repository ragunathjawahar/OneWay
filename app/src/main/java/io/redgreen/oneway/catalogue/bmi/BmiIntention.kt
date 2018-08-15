package io.redgreen.oneway.catalogue.bmi

import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem

sealed class BmiIntention
data class ChangeWeightIntention(val weightInKg: Double) : BmiIntention()
data class ChangeHeightIntention(val heightInCm: Double) : BmiIntention()
data class ChangeMeasurementSystemIntention(val measurementSystem: MeasurementSystem) : BmiIntention()
