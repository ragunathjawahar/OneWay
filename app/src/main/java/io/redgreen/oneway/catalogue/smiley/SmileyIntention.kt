package io.redgreen.oneway.catalogue.smiley

import arrow.core.None
import arrow.core.Option
import arrow.core.Some

sealed class SmileyIntention

@Suppress("DataClassPrivateConstructor")
data class PickSmileyIntention private constructor(
    val characterOption: Option<String>
) : SmileyIntention() {
  companion object {
    val NONE = PickSmileyIntention(None)

    fun of(character: String): PickSmileyIntention =
        PickSmileyIntention(Some(character))
  }
}
