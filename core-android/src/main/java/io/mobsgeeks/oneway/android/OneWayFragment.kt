package io.mobsgeeks.oneway.android

import android.os.Parcelable
import io.mobsgeeks.oneway.NoOpStateSerializer
import io.mobsgeeks.oneway.StateSerializer
import io.mobsgeeks.oneway.android.barebones.BareBonesOneWayFragment
import io.mobsgeeks.oneway.android.barebones.Persister

abstract class OneWayFragment<S : Parcelable> : BareBonesOneWayFragment<S, S>() {
  override val stateSerializer: StateSerializer<S, S>
    get() = NoOpStateSerializer()

  override val persister: Persister<S>
    get() = ParcelablePersister()
}
