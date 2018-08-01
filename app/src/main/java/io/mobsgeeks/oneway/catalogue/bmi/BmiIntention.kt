package io.mobsgeeks.oneway.catalogue.bmi

sealed class BmiIntention
data class ChangeWeightIntention(val weightInKg: Double) : BmiIntention()
