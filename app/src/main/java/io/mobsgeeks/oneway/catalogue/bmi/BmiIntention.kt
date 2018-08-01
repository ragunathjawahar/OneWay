package io.mobsgeeks.oneway.catalogue.bmi

sealed class BmiIntention
data class ChangeWeightIntention(val weightInKg: Double) : BmiIntention()
data class ChangeHeightIntention(val heightInCm: Double) : BmiIntention()
data class ChangeMeasurementSystemIntention(val measurementSystem: MeasurementSystem) : BmiIntention()
