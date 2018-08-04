package io.mobsgeeks.oneway.android

import android.os.Bundle
import android.os.Parcelable
import io.mobsgeeks.oneway.NoOpStateSerializer
import io.mobsgeeks.oneway.StateSerializer

abstract class OneWayActivity<S : Parcelable> : BareBonesOneWayActivity<S, S>() {
  override val stateSerializer: StateSerializer<S, S>
    get() = NoOpStateSerializer()

  override val persister: Persister<S>
    get() = object : Persister<S> {
      private val stateKey = "one_way_persisted_state"

      override fun write(persistableState: S, bundle: Bundle) =
          bundle.putParcelable(stateKey, persistableState)

      override fun read(bundle: Bundle): S =
          bundle.getParcelable(stateKey)
    }
}
