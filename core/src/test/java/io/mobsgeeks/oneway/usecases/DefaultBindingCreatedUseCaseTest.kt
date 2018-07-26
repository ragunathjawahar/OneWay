package io.mobsgeeks.oneway.usecases

import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.Binding.*
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class DefaultBindingCreatedUseCaseTest {
  private val bindings = PublishSubject.create<Binding>()
  private val initialState = "Idli & Chutney"
  private val defaultBindingCreatedUseCase = DefaultBindingCreatedUseCase(initialState)
  private val testObserver = bindings.compose(defaultBindingCreatedUseCase).test()

  @Test fun `it does nothing for DESTROYED and RESTORED events`() {
    // when
    bindings.onNext(DESTROYED)
    bindings.onNext(RESTORED)

    // then
    with(testObserver) {
      assertNoErrors()
      assertNoValues()
    }
  }

  @Test fun `it emits an initial state for CREATED event`() {
    // when
    bindings.onNext(CREATED)

    // then
    with(testObserver) {
      assertNoErrors()
      assertValue(initialState)
    }
  }
}
