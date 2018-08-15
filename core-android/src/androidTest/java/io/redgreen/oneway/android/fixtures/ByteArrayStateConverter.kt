package io.redgreen.oneway.android.fixtures

import io.redgreen.oneway.StateConverter

open class ByteArrayStateConverter : StateConverter<String, ByteArray> {
  override fun to(state: String): ByteArray =
      state.toByteArray()

  override fun from(persistableState: ByteArray): String =
      String(persistableState)
}
