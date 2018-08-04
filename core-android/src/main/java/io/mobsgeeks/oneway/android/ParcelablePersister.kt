package io.mobsgeeks.oneway.android

import android.os.Bundle
import android.os.Parcelable
import io.mobsgeeks.oneway.android.barebones.Persister

class ParcelablePersister<S: Parcelable> : Persister<S> {
  private val stateKey = "one_way_state"

  override fun write(persistableState: S, bundle: Bundle) =
      bundle.putParcelable(stateKey, persistableState)

  override fun read(bundle: Bundle): S? =
      bundle.getParcelable(stateKey)
}
