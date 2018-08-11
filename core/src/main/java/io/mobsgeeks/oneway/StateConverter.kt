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
 * Converts state represented by the model during runtime to a format that
 * is suitable for persistence and vice-versa.
 */
interface StateConverter<S, P> {
  /** @param state state to be persisted. */
  fun to(state: S): P

  /** @param persistableState persistent state. */
  fun from(persistableState: P): S
}
