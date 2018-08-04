package io.mobsgeeks.oneway.android.barebones

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import io.reactivex.Observable
import kotlin.LazyThreadSafetyMode.NONE

abstract class BareBonesOneWayFragment<S, P> :
    Fragment(),
    AndroidMviContract<S, P> {
  private val androidMviDelegate: AndroidMviDelegate<S, P> by lazy(NONE) {
    AndroidMviDelegate(this)
  }

  override val timeline: Observable<S>
    get() = androidMviDelegate.timeline

  @CallSuper override fun onStart() {
    super.onStart()
    androidMviDelegate.bind()
  }

  @CallSuper override fun onStop() {
    androidMviDelegate.unbind()
    super.onStop()
  }

  @CallSuper override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    androidMviDelegate.saveState(outState)
  }

  @CallSuper override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    androidMviDelegate.restoreState(savedInstanceState)
  }
}
