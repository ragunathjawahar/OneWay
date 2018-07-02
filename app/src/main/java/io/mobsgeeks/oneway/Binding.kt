package io.mobsgeeks.oneway

/** An enum that denotes the nature of the subscription. */
enum class Binding {
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
   * The subscription has been disposed. This event is never delivered to the bindings
   * stream when the subscription is active. This event is helpful only in cases where
   * the model's lifecycle has to work with an external entity.
   */
  DESTROYED
}
