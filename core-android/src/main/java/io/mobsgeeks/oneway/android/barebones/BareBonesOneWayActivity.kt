package io.mobsgeeks.oneway.android.barebones

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatActivity
import io.reactivex.Observable
import kotlin.LazyThreadSafetyMode.NONE

abstract class BareBonesOneWayActivity<S, P> :
    AppCompatActivity(),
    AndroidMviContract<S, P> {
  private val androidMviDelegate: AndroidMviDelegate<S, P> by lazy(NONE) {
    AndroidMviDelegate(this)
  }

  override val timeline: Observable<S>
    get() = androidMviDelegate.timeline

  @CallSuper override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    androidMviDelegate.restoreState(savedInstanceState)
  }

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
}
