package io.mobsgeeks.oneway.catalogue.budapest

sealed class BudapestIntention
data class EnterNameIntention(val name: String) : BudapestIntention()
