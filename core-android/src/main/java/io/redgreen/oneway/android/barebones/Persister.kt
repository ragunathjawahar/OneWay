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
package io.redgreen.oneway.android.barebones

import android.os.Bundle

/** Persists a state from and to the [Bundle]. */
interface Persister<P> {
  /**
   * Writes the `persistableState` into the `bundle` in an appropriate
   * format.
   */
  fun write(persistableState: P, bundle: Bundle)

  /**
   * Reads a previously persisted state from the `bundle` appropriately
   * and returns it in a persistable format.
   */
  fun read(bundle: Bundle): P?
}
