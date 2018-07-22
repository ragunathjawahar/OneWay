package io.mobsgeeks.oneway

class NoOpStateSerializer<S> : StateSerializer<S, S> {
  override fun serialize(state: S): S = state
  override fun deserialize(persistentState: S): S = persistentState
}
