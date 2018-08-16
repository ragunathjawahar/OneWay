package io.redgreen.oneway.catalogue.signup.drivers

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.catalogue.base.SchedulersProvider
import io.redgreen.oneway.catalogue.signup.SignUpState
import io.redgreen.oneway.catalogue.signup.SignUpView
import io.redgreen.oneway.catalogue.signup.form.Field
import io.redgreen.oneway.catalogue.signup.form.PhoneNumberCondition
import io.redgreen.oneway.catalogue.signup.form.UsernameCondition
import io.redgreen.oneway.catalogue.signup.form.WhichField.PHONE_NUMBER
import io.redgreen.oneway.catalogue.signup.form.WhichField.USERNAME
import io.redgreen.oneway.drivers.ViewDriver
import java.util.concurrent.TimeUnit

class SignUpViewDriver(
    private val view: SignUpView,
    private val schedulersProvider: SchedulersProvider,
    private val errorThresholdMillis: Long
) : ViewDriver<SignUpState> {
  private val displayErrorEventsSubject = PublishSubject.create<DisplayErrorEvent>()

  override fun render(source: Observable<SignUpState>): Disposable {
    val delayedSource = source
        .debounce(errorThresholdMillis, TimeUnit.MILLISECONDS, schedulersProvider.computation())
        .observeOn(schedulersProvider.ui())

    val phoneNumberFields = source.map { it.phoneNumberField }
    val usernameFields = source.map { it.usernameField }
    val delayedPhoneNumberFields = delayedSource.map { it.phoneNumberField }
    val delayedUsernameFields = delayedSource.map { it.usernameField }

    val compositeDisposable = CompositeDisposable()
    compositeDisposable.addAll(
        renderValidPhoneNumbers(delayedPhoneNumberFields),
        renderInvalidPhoneNumbers(delayedPhoneNumberFields),
        renderValidUsernames(delayedUsernameFields),
        renderInvalidUsernames(delayedUsernameFields),

        renderPhoneNumbersWithImmediateFeedback(phoneNumberFields),
        renderUsernamesWithImmediateFeedback(usernameFields)
    )
    return compositeDisposable
  }

  fun displayErrorEvents(): Observable<DisplayErrorEvent> =
      displayErrorEventsSubject.distinctUntilChanged().hide()

  private fun renderValidPhoneNumbers(
      phoneNumberFields: Observable<Field<PhoneNumberCondition>>
  ): Disposable {
    return phoneNumberFields
        .filter { it.untouched || it.unmetConditions.isEmpty() }
        .distinctUntilChanged()
        .filter { !it.untouched }
        .doOnNext { displayErrorEventsSubject.onNext(DisplayErrorEvent(PHONE_NUMBER, false)) }
        .subscribe { view.hidePhoneNumberError() }
  }

  private fun renderInvalidPhoneNumbers(
      phoneNumberFields: Observable<Field<PhoneNumberCondition>>
  ): Disposable {
    return phoneNumberFields
        .filter { it.unmetConditions.isNotEmpty() }
        .distinctUntilChanged()
        .doOnNext { displayErrorEventsSubject.onNext(DisplayErrorEvent(PHONE_NUMBER, true)) }
        .subscribe { view.showPhoneNumberErrors(it.unmetConditions) }
  }

  private fun renderValidUsernames(
      usernameFields: Observable<Field<UsernameCondition>>
  ): Disposable {
    return usernameFields
        .filter { it.untouched || it.unmetConditions.isEmpty() }
        .distinctUntilChanged()
        .filter { !it.untouched }
        .doOnNext { displayErrorEventsSubject.onNext(DisplayErrorEvent(USERNAME, false)) }
        .subscribe { view.hideUsernameError() }
  }

  private fun renderInvalidUsernames(
      usernameFields: Observable<Field<UsernameCondition>>
  ): Disposable {
    return usernameFields
        .filter { it.unmetConditions.isNotEmpty() }
        .distinctUntilChanged()
        .doOnNext { displayErrorEventsSubject.onNext(DisplayErrorEvent(USERNAME, true)) }
        .subscribe { view.showUsernameErrors(it.unmetConditions) }
  }

  private fun renderPhoneNumbersWithImmediateFeedback(
      phoneNumberFields: Observable<Field<PhoneNumberCondition>>
  ): Disposable {
    return phoneNumberFields
        .filter { it.displayError }
        .distinctUntilChanged()
        .subscribe {
          if (it.unmetConditions.isEmpty()) {
            view.hidePhoneNumberError()
          } else {
            view.showPhoneNumberErrors(it.unmetConditions)
          }
        }
  }

  private fun renderUsernamesWithImmediateFeedback(
      usernameFields: Observable<Field<UsernameCondition>>
  ): Disposable {
    return usernameFields
        .filter { it.displayError }
        .distinctUntilChanged()
        .subscribe {
          if (it.unmetConditions.isEmpty()) {
            view.hideUsernameError()
          } else {
            view.showUsernameErrors(it.unmetConditions)
          }
        }
  }
}
