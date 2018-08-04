package io.mobsgeeks.oneway.android.barebones

import android.os.Bundle

interface Persister<P> {
  fun write(persistableState: P, bundle: Bundle)
  fun read(bundle: Bundle): P?
}
