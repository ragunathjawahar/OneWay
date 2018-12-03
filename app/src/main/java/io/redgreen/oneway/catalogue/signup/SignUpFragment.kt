package io.redgreen.oneway.catalogue.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.focusChanges
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.android.OneWayFragment
import io.redgreen.oneway.catalogue.R
import io.redgreen.oneway.catalogue.base.DefaultSchedulersProvider
import io.redgreen.oneway.catalogue.base.extensions.fastLazy
import io.redgreen.oneway.catalogue.signup.drivers.SignUpViewDriver
import io.redgreen.oneway.catalogue.signup.form.PhoneNumberCondition
import io.redgreen.oneway.catalogue.signup.form.UsernameCondition
import io.redgreen.oneway.catalogue.signup.form.Validator
import io.redgreen.oneway.catalogue.signup.usecases.SignUpUseCases
import kotlinx.android.synthetic.main.sign_up_fragment.*

class SignUpFragment : OneWayFragment<SignUpState>(), SignUpView {
  private val intentions by fastLazy {
    SignUpIntentions(
        this,
        phoneNumberEditText.textChanges().skipInitialValue(),
        phoneNumberEditText.focusChanges().skipInitialValue(),
        usernameEditText.textChanges().skipInitialValue(),
        usernameEditText.focusChanges().skipInitialValue(),
        signUpButton.clicks()
    )
  }

  private val validator by fastLazy {
    Validator()
  }

  private val useCases by fastLazy {
    SignUpUseCases(
        sourceCopy,
        validator
    )
  }

  private val schedulersProvider by fastLazy {
    DefaultSchedulersProvider()
  }

  private val viewDriver by fastLazy {
    val errorThresholdMillis = resources
        .getInteger(R.integer.error_threshold_millis)
        .toLong()
    return@fastLazy SignUpViewDriver(this, schedulersProvider, errorThresholdMillis)
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.sign_up_fragment, container, false)
  }

  override fun source(
      sourceLifecycleEvents: Observable<SourceLifecycleEvent>,
      sourceCopy: Observable<SignUpState>
  ): Observable<SignUpState> {
    return SignUpModel
        .createSource(
            intentions.stream(),
            viewDriver.displayErrorEvents(),
            sourceLifecycleEvents,
            useCases
        )
  }

  override fun sink(source: Observable<SignUpState>): Disposable =
      viewDriver.render(source)

  override fun getPhoneNumber(): String =
      phoneNumberEditText.text.toString()

  override fun showPhoneNumberErrors(unmetConditions: Set<PhoneNumberCondition>) {
    phoneNumberTextInputLayout.error = unmetConditions.toString()
  }

  override fun hidePhoneNumberError() {
    phoneNumberTextInputLayout.error = null
  }

  override fun getUsername(): String =
      usernameEditText.text.toString()

  override fun showUsernameErrors(unmetConditions: Set<UsernameCondition>) {
    usernameTextInputLayout.error = unmetConditions.toString()
  }

  override fun hideUsernameError() {
    usernameTextInputLayout.error = null
  }
}
