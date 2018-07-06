package io.mobsgeeks.oneway

/** A mechanism that enables saving and restoring state. */
interface Persister<S, P> {
  /**
   * @param state The state that has to be persisted.
   */
  fun serialize(state: S): P

  /**
   * @param persistentState The persistent state.
   */
  fun deserialize(persistentState: P): S
}
