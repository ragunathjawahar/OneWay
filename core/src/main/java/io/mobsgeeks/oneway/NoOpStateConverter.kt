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
package io.mobsgeeks.oneway

/**
 * A [StateConverter] that does not perform any conversion between the state
 * used by the model and the state representation required for persistence.
 * Use this class if the state used by the model is also suitable for
 * persistence.
 */
class NoOpStateConverter<S> : StateConverter<S, S> {
  override fun to(state: S): S = state
  override fun from(persistableState: S): S = persistableState
}
