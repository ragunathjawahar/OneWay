package io.mobsgeeks.oneway.catalogue.budapest

sealed class BudapestIntention
data class NameChangeIntention(val name: String) : BudapestIntention()
