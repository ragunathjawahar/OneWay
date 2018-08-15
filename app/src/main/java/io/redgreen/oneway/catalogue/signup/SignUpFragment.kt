package io.redgreen.oneway.catalogue.signup

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.redgreen.oneway.SourceEvent
import io.redgreen.oneway.android.OneWayFragment
import io.redgreen.oneway.catalogue.signup.form.PhoneNumberCondition
import io.redgreen.oneway.catalogue.signup.form.UsernameCondition

class SignUpFragment : OneWayFragment<SignUpState>(), SignUpView {
  override fun source(
      sourceEvents: Observable<SourceEvent>,
      timeline: Observable<SignUpState>
  ): Observable<SignUpState> {
    TODO("not implemented")
  }

  override fun sink(source: Observable<SignUpState>): Disposable {
    TODO("not implemented")
  }

  override fun showPhoneNumberErrors(unmetConditions: Set<PhoneNumberCondition>) {
    TODO("not implemented")
  }

  override fun hidePhoneNumberError() {
    TODO("not implemented")
  }

  override fun showUsernameErrors(unmetConditions: Set<UsernameCondition>) {
    TODO("not implemented")
  }

  override fun hideUsernameError() {
    TODO("not implemented")
  }
}
