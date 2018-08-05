package io.mobsgeeks.oneway.android.fixtures

import android.os.Bundle
import io.mobsgeeks.oneway.android.barebones.Persister

class ByteArrayPersister : Persister<ByteArray> {
  private val stateKey = "state"

  override fun write(persistableState: ByteArray, bundle: Bundle) =
      bundle.putByteArray(stateKey, persistableState)

  override fun read(bundle: Bundle): ByteArray? =
      bundle.getByteArray(stateKey)
}
