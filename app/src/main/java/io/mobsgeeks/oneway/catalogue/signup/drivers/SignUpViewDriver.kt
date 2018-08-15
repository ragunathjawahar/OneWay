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
    val delayedSource = source
        .debounce(
            SHOW_ERROR_DEBOUNCE_MILLIS,
            TimeUnit.MILLISECONDS,
            schedulersProvider.computation()
        )
        .observeOn(schedulersProvider.ui())

    // Phone number
    val phoneNumberFields = delayedSource
        .map { it.phoneNumberField }

    val validPhoneNumberFields = phoneNumberFields
        .filter { it.untouched || it.unmetConditions.isEmpty() }
        .distinctUntilChanged()

    val invalidPhoneNumberFields = phoneNumberFields
        .filter { it.unmetConditions.isNotEmpty() }
        .distinctUntilChanged()

    // Username
    val usernameFields = delayedSource
        .map { it.usernameField }

    val validUsernameFields = usernameFields
        .filter { it.untouched || it.unmetConditions.isEmpty() }
        .distinctUntilChanged()

    val invalidUsernameFields = usernameFields
        .filter { it.unmetConditions.isNotEmpty() }
        .distinctUntilChanged()

    // Subscriptions
    val compositeDisposable = CompositeDisposable()
    compositeDisposable.addAll(
        validPhoneNumberFields
            .subscribe { view.hidePhoneNumberError() },

        invalidPhoneNumberFields
            .subscribe { view.showPhoneNumberErrors(it.unmetConditions) },

        validUsernameFields
            .subscribe { view.hideUsernameError() },

        invalidUsernameFields
            .subscribe { view.showUsernameErrors(it.unmetConditions) }
    )
    return compositeDisposable
  }
}
