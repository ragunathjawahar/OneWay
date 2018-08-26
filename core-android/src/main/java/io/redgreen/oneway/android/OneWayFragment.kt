/*
 * Copyright (C) 2018 Ragunath Jawahar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.redgreen.oneway.android

import android.os.Parcelable
import io.redgreen.oneway.NoOpStateConverter
import io.redgreen.oneway.StateConverter
import io.redgreen.oneway.android.barebones.BareBonesOneWayFragment
import io.redgreen.oneway.android.barebones.Persister

/** A base [android.support.v4.app.Fragment] class with all the MVI niceties. */
abstract class OneWayFragment<S : Parcelable> : BareBonesOneWayFragment<S, S>() {
  override val stateConverter: StateConverter<S, S>
    get() = NoOpStateConverter()

  override val persister: Persister<S>
    get() = ParcelablePersister(this::class.java.name)
}
