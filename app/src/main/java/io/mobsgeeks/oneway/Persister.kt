package io.mobsgeeks.oneway

/** A mechanism that enables saving and restoring the model's state. */
interface Persister<S, P> {
  /**
   * @param state The model's state that has to be persisted.
   */
  fun serialize(state: S): P
}
