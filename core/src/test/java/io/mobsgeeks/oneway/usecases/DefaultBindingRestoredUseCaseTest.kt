package io.mobsgeeks.oneway.usecases

import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.Binding.*
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class DefaultBindingRestoredUseCaseTest {
  private val bindings = PublishSubject.create<Binding>()
  private val timeline = PublishSubject.create<String>()
  private val defaultBindingRestoredUseCase = DefaultBindingRestoredUseCase(timeline)
  private val testObserver = bindings.compose(defaultBindingRestoredUseCase).test()

  @Test fun `it does nothing for CREATED and DESTROYED events`() {
    // when
    bindings.onNext(CREATED)
    bindings.onNext(DESTROYED)

    // then
    with(testObserver) {
      assertNoErrors()
      assertNoValues()
    }
  }

  @Test fun `it emits the last known state for RESTORED`() {
    // given
    val lastKnownCat = "Schrodinger's Cat"
    timeline.onNext(lastKnownCat)

    // when
    bindings.onNext(RESTORED)

    // then
    with(testObserver) {
      assertNoErrors()
      assertValue(lastKnownCat)
    }
  }
}
