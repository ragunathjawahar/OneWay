package io.redgreen.oneway.catalogue.smiley

sealed class SmileyIntention
data class PickSmileyIntention(val smiley: String) : SmileyIntention()
