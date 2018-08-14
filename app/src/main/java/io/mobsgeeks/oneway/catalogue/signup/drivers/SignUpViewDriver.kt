package io.mobsgeeks.oneway.catalogue.signup.drivers

import io.mobsgeeks.oneway.catalogue.base.SchedulersProvider
import io.mobsgeeks.oneway.catalogue.signup.SignUpState
import io.mobsgeeks.oneway.catalogue.signup.SignUpView
import io.mobsgeeks.oneway.drivers.ViewDriver
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class SignUpViewDriver(
    private val view: SignUpView,
    private val schedulersProvider: SchedulersProvider
) : ViewDriver<SignUpState> {
  companion object {
    const val SHOW_ERROR_THRESHOLD_MILLIS = 200L
  }

  override fun render(source: Observable<SignUpState>): Disposable {
    return source.subscribe {  }
  }
}
