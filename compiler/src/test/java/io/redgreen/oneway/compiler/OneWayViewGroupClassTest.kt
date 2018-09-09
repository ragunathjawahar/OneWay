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
package io.redgreen.oneway.compiler

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class OneWayViewGroupClassTest {
  @Test fun `it creates an abstract class`() {
    // given
    val parentClassFqcn = "android.support.constraint.ConstraintLayout"
    val classToGenerateFqcn = "io.redgreen.oneway.widget.OneWayConstraintLayout"
    val oneWayViewGroup = OneWayViewGroupClass(parentClassFqcn, classToGenerateFqcn)

    // when
    val brewedJavaFile = oneWayViewGroup.brewJava()

    // then
    val generatedFileContent = brewedJavaFile.toString()
    assertThat(generatedFileContent)
        .isEqualTo(
            """
              package io.redgreen.oneway.widget;

              import android.content.Context;
              import android.os.Bundle;
              import android.os.Parcelable;
              import android.support.annotation.CallSuper;
              import android.support.annotation.NonNull;
              import android.support.annotation.Nullable;
              import android.support.constraint.ConstraintLayout;
              import android.util.AttributeSet;
              import io.reactivex.Observable;
              import io.redgreen.oneway.NoOpStateConverter;
              import io.redgreen.oneway.StateConverter;
              import io.redgreen.oneway.android.ParcelablePersister;
              import io.redgreen.oneway.android.barebones.AndroidMviContract;
              import io.redgreen.oneway.android.barebones.AndroidMviDelegate;
              import io.redgreen.oneway.android.barebones.Persister;

              public abstract class OneWayConstraintLayout<S extends Parcelable> extends ConstraintLayout implements AndroidMviContract<S, S> {
                private static final String KEY_SUPER_CLASS_STATE = "super_class_state";

                private final AndroidMviDelegate<S, S> androidMviDelegate = new AndroidMviDelegate<>(this);

                public OneWayConstraintLayout(Context context) {
                  super(context);
                }

                public OneWayConstraintLayout(Context context, AttributeSet attrs) {
                  super(context, attrs);
                }

                public OneWayConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
                  super(context, attrs, defStyleAttr);
                }

                @NonNull
                @Override
                public StateConverter<S, S> getStateConverter() {
                  return new NoOpStateConverter<>();
                }

                @NonNull
                @Override
                public Persister<S> getPersister() {
                  return new ParcelablePersister<>(KEY_SUPER_CLASS_STATE);
                }

                @NonNull
                @Override
                public final Observable<S> getTimeline() {
                  return androidMviDelegate.getTimeline();
                }

                @CallSuper
                @Override
                protected void onAttachedToWindow() {
                  super.onAttachedToWindow();
                  androidMviDelegate.bind();
                }

                @CallSuper
                @Override
                protected void onDetachedFromWindow() {
                  androidMviDelegate.unbind();
                  super.onDetachedFromWindow();
                }

                @Nullable
                @CallSuper
                @Override
                protected Parcelable onSaveInstanceState() {
                  Parcelable viewState = super.onSaveInstanceState();
                  Bundle outState = new Bundle();
                  outState.putParcelable(KEY_SUPER_CLASS_STATE, viewState);
                  androidMviDelegate.saveState(outState);
                  return outState;
                }

                @CallSuper
                @Override
                protected void onRestoreInstanceState(Parcelable state) {
                  androidMviDelegate.restoreState((Bundle) state);
                  super.onRestoreInstanceState(((Bundle) state).getParcelable(KEY_SUPER_CLASS_STATE));
                }
              }

            """.trimIndent()
        )
  }
}
