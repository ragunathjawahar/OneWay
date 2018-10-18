package io.redgreen.oneway.catalogue.smiley

data class SmileyState(val smiley: String) {
  companion object {
    fun initial(smiley: String): SmileyState =
        SmileyState(smiley)
  }

  fun setSmiley(smiley: String): SmileyState =
      copy(smiley = smiley)
}
