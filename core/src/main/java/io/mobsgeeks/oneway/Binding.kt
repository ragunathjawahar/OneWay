package io.mobsgeeks.oneway

/** An enum that denotes the nature of the subscription. */
enum class Binding { // TODO(rj) 26/Jul/18 - Consider renaming this to SourceEvent?
  /**
   * The subscription happened for the first time, it also denotes that a
   * previous state is non-existent.
   */
  CREATED,

  /**
   * The subscription is being restored, it also denotes that a saved state
   * is available.
   */
  RESTORED,

  /**
   * The subscription is about to be disposed. This event is emitted just
   * before the stream is disposed.
   */
  DESTROYED
}
