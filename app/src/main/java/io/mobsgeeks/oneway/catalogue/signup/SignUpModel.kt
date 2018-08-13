package io.mobsgeeks.oneway.catalogue.signup

import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.usecases.SourceCreatedUseCase
import io.reactivex.Observable

object SignUpModel {
  fun createSource(
      sourceEvents: Observable<SourceEvent>,
      sourceCreatedUseCase: SourceCreatedUseCase<SignUpState>
  ): Observable<SignUpState> {
    return sourceEvents.compose(sourceCreatedUseCase)
  }
}
