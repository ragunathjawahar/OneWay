package io.mobsgeeks.oneway.android.fixtures

import io.mobsgeeks.oneway.StateConverter

open class ByteArrayStateConverter : StateConverter<String, ByteArray> {
  override fun to(state: String): ByteArray =
      state.toByteArray()

  override fun from(persistableState: ByteArray): String =
      String(persistableState)
}
