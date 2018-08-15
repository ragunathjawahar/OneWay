package io.mobsgeeks.oneway.catalogue.signup.drivers

import io.mobsgeeks.oneway.catalogue.base.SchedulersProvider
import io.mobsgeeks.oneway.catalogue.signup.SignUpState
import io.mobsgeeks.oneway.catalogue.signup.SignUpView
import io.mobsgeeks.oneway.drivers.ViewDriver
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class SignUpViewDriver(
    private val view: SignUpView,
    private val schedulersProvider: SchedulersProvider
) : ViewDriver<SignUpState> {
  companion object {
    const val SHOW_ERROR_DEBOUNCE_MILLIS = 200L
  }

  override fun render(source: Observable<SignUpState>): Disposable {
    val compositeDisposable = CompositeDisposable()
    val delayedSource = source
        .debounce(
            SHOW_ERROR_DEBOUNCE_MILLIS,
            TimeUnit.MILLISECONDS,
            schedulersProvider.computation()
        )
        .observeOn(schedulersProvider.ui())

    compositeDisposable.addAll(
        delayedSource
            .map { it.phoneNumberField }
            .filter { it.untouched || it.unmetConditions.isEmpty() }
            .distinctUntilChanged()
            .subscribe { view.hidePhoneNumberError() },

        delayedSource
            .map { it.usernameField }
            .filter { it.untouched || it.unmetConditions.isEmpty() }
            .distinctUntilChanged()
            .subscribe { view.hideUsernameError() }
    )

    return compositeDisposable
  }
}
