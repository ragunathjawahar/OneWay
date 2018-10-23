package io.redgreen.oneway.catalogue.smiley

import arrow.core.Option

sealed class SmileyIntention

data class PickSmileyIntention(
    val smiley: Option<String>
) : SmileyIntention()
