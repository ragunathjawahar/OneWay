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
package io.redgreen.oneway

/** Represents the various lifecycle events of the state source. */
enum class SourceEvent {
  /**
   * The source has been created for the first time, it also denotes that
   * a previous state does not exist.
   */
  CREATED,

  /**
   * The source is being restored after it was destroyed, it also denotes
   * that a saved state is available via `sourceCopy`.
   */
  RESTORED,

  /**
   * The source is about to be disposed. This event is emitted just before
   * the source stream is disposed.
   */
  DESTROYED
}
