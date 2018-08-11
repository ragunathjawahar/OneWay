package io.mobsgeeks.oneway.android

import android.os.Parcelable
import io.mobsgeeks.oneway.NoOpStateConverter
import io.mobsgeeks.oneway.StateConverter
import io.mobsgeeks.oneway.android.barebones.BareBonesOneWayFragment
import io.mobsgeeks.oneway.android.barebones.Persister

abstract class OneWayFragment<S : Parcelable> : BareBonesOneWayFragment<S, S>() {
  override val stateConverter: StateConverter<S, S>
    get() = NoOpStateConverter()

  override val persister: Persister<S>
    get() = ParcelablePersister()
}
