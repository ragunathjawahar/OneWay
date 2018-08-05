package io.mobsgeeks.oneway.android.fixtures

import io.mobsgeeks.oneway.StateSerializer

class ByteArrayStateSerializer : StateSerializer<String, ByteArray> {
  override fun serialize(state: String): ByteArray =
      state.toByteArray()

  override fun deserialize(persistableState: ByteArray): String =
      String(persistableState)
}
