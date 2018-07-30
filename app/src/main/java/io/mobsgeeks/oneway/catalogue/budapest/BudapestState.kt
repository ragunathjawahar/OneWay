package io.mobsgeeks.oneway.catalogue.budapest

data class BudapestState(private val name: String) {
  companion object {
    val STRANGER = BudapestState("")
  }
}
