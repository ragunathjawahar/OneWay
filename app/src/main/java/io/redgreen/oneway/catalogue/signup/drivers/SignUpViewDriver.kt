package io.redgreen.oneway.catalogue.signup.drivers

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.catalogue.base.SchedulersProvider
import io.redgreen.oneway.catalogue.signup.SignUpState
import io.redgreen.oneway.catalogue.signup.SignUpView
import io.redgreen.oneway.drivers.ViewDriver
import java.util.concurrent.TimeUnit

class SignUpViewDriver(
    private val view: SignUpView,
    private val schedulersProvider: SchedulersProvider
) : ViewDriver<SignUpState> {
  companion object {
    const val SHOW_ERROR_DEBOUNCE_MILLIS = 200L
  }

  private val displayErrorEventsSubject = PublishSubject.create<DisplayErrorEvent>()

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
            .doOnNext { displayErrorEventsSubject.onNext(DisplayPhoneNumberErrorEvent(false)) }
            .subscribe { view.hidePhoneNumberError() },

        invalidPhoneNumberFields
            .doOnNext { displayErrorEventsSubject.onNext(DisplayPhoneNumberErrorEvent(true)) }
            .subscribe { view.showPhoneNumberErrors(it.unmetConditions) },

        validUsernameFields
            .doOnNext { displayErrorEventsSubject.onNext(DisplayUsernameErrorEvent(false)) }
            .subscribe { view.hideUsernameError() },

        invalidUsernameFields
            .doOnNext { displayErrorEventsSubject.onNext(DisplayUsernameErrorEvent(true)) }
            .subscribe { view.showUsernameErrors(it.unmetConditions) }
    )
    return compositeDisposable
  }

  fun displayErrorEvents(): Observable<DisplayErrorEvent> =
      displayErrorEventsSubject.hide()
}
