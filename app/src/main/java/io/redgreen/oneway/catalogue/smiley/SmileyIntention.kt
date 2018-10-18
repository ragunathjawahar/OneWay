package io.redgreen.oneway.catalogue.smiley

sealed class SmileyIntention
data class ChooseSmileyIntention(val smiley: String) : SmileyIntention()
