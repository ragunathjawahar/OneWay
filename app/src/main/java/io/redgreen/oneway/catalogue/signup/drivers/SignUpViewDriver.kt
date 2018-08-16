package io.redgreen.oneway.catalogue.signup.drivers

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.catalogue.base.SchedulersProvider
import io.redgreen.oneway.catalogue.signup.SignUpState
import io.redgreen.oneway.catalogue.signup.SignUpView
import io.redgreen.oneway.catalogue.signup.form.*
import io.redgreen.oneway.catalogue.signup.form.WhichField.PHONE_NUMBER
import io.redgreen.oneway.catalogue.signup.form.WhichField.USERNAME
import io.redgreen.oneway.drivers.ViewDriver
import java.util.concurrent.TimeUnit.MILLISECONDS

class SignUpViewDriver(
    private val view: SignUpView,
    private val schedulersProvider: SchedulersProvider,
    private val errorThresholdMillis: Long
) : ViewDriver<SignUpState> {
  private val displayErrorEventsSubject = PublishSubject.create<DisplayErrorEvent>()

  private val showPhoneNumberErrorUiEffect: (Set<PhoneNumberCondition>) -> Unit =
      { unmetConditions -> view.showPhoneNumberErrors(unmetConditions) }

  private val hidePhoneNumberErrorUiEffect: () -> Unit =
      { view.hidePhoneNumberError() }

  private val showUsernameErrorUiEffect: (Set<UsernameCondition>) -> Unit =
      { unmetConditions -> view.showUsernameErrors(unmetConditions) }

  private val hideUsernameErrorUiEffect: () -> Unit =
      { view.hideUsernameError() }

  override fun render(source: Observable<SignUpState>): Disposable {
    val delayedSource = source
        .debounce(errorThresholdMillis, MILLISECONDS, schedulersProvider.computation())
        .observeOn(schedulersProvider.ui())
    val delayedPhoneNumberFields = delayedSource.map { it.phoneNumberField }
    val delayedUsernameFields = delayedSource.map { it.usernameField }

    val phoneNumberFields = source.map { it.phoneNumberField }
    val usernameFields = source.map { it.usernameField }

    return CompositeDisposable().apply {
      addAll(
          handleValidFields(delayedPhoneNumberFields, PHONE_NUMBER, hidePhoneNumberErrorUiEffect),
          handleValidFields(delayedUsernameFields, USERNAME, hideUsernameErrorUiEffect),

          handleInvalidFields(delayedPhoneNumberFields, PHONE_NUMBER, showPhoneNumberErrorUiEffect),
          handleInvalidFields(delayedUsernameFields, USERNAME, showUsernameErrorUiEffect),

          handleFieldsThatDisplayError(phoneNumberFields, hidePhoneNumberErrorUiEffect, showPhoneNumberErrorUiEffect),
          handleFieldsThatDisplayError(usernameFields, hideUsernameErrorUiEffect, showUsernameErrorUiEffect)
      )
    }
  }

  fun displayErrorEvents(): Observable<DisplayErrorEvent> =
      displayErrorEventsSubject.distinctUntilChanged().hide()

  private fun <T> handleValidFields(
      fields: Observable<Field<T>>,
      whichField: WhichField,
      hideErrorUiEffect: () -> Unit
  ): Disposable where T : Enum<T>, T : Condition {
    return fields
        .filter { it.touched && !it.hasErrors() }
        .distinctUntilChanged()
        .doOnNext { displayErrorEventsSubject.onNext(DisplayErrorEvent(whichField, false)) }
        .subscribe { hideErrorUiEffect() }
  }

  private fun <T> handleInvalidFields(
      fields: Observable<Field<T>>,
      whichField: WhichField,
      showErrorUiEffect: (Set<T>) -> Unit
  ): Disposable where T : Enum<T>, T : Condition {
    return fields
        .map { it.unmetConditions }
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()
        .doOnNext { displayErrorEventsSubject.onNext(DisplayErrorEvent(whichField, true)) }
        .subscribe { showErrorUiEffect(it) }
  }

  private fun <T> handleFieldsThatDisplayError(
      fields: Observable<Field<T>>,
      hideErrorUiEffect: () -> Unit,
      showErrorUiEffect: (Set<T>) -> Unit
  ): Disposable where T : Enum<T>, T : Condition {
    return fields
        .filter { it.displayError }
        .map { it.unmetConditions }
        .distinctUntilChanged()
        .subscribe { if (it.isEmpty()) hideErrorUiEffect() else showErrorUiEffect(it) }
  }
}
