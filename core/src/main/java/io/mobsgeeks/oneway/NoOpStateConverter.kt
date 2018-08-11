package io.mobsgeeks.oneway

class NoOpStateConverter<S> : StateConverter<S, S> {
  override fun to(state: S): S = state
  override fun from(persistableState: S): S = persistableState
}
