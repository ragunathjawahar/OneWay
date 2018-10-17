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
import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatActivity
import io.reactivex.Observable
import kotlin.LazyThreadSafetyMode.NONE

/**
 * A basic implementation and usage of [AndroidMviContract] and [AndroidMviDelegate].
 * Use this as a reference when implementing your own base classes in case of
 * necessity. Most of the time, [io.redgreen.oneway.android.OneWayActivity] is
 * sufficient to get started with.
 */
abstract class BareBonesOneWayActivity<S, P> :
    AppCompatActivity(),
    AndroidMviContract<S, P> {
  private val androidMviDelegate: AndroidMviDelegate<S, P> by lazy(NONE) {
    AndroidMviDelegate(this)
  }

  override val sourceCopy: Observable<S>
    get() = androidMviDelegate.sourceCopy

  @CallSuper override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    androidMviDelegate.restoreState(savedInstanceState)
  }

  @CallSuper override fun onStart() {
    super.onStart()
    androidMviDelegate.connect()
  }

  @CallSuper override fun onStop() {
    androidMviDelegate.disconnect()
    super.onStop()
  }

  @CallSuper override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    androidMviDelegate.saveState(outState)
  }
}
