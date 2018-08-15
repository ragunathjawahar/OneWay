package io.redgreen.oneway.catalogue.budapest

sealed class BudapestIntention
data class EnterNameIntention(val name: String) : BudapestIntention()
