package io.redgreen.oneway.usecases

import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.SourceLifecycleEvent.CREATED
import io.redgreen.oneway.SourceLifecycleEvent.RESTORED
import org.junit.Test

class SourceCreatedUseCaseTest {
  private val sourceLifecycleEventsSubject = PublishSubject.create<SourceLifecycleEvent>()
  private val initialState = "Idli & Chutney"
  private val sourceCreatedUseCase = SourceCreatedUseCase(initialState)
  private val testObserver = sourceLifecycleEventsSubject.compose(sourceCreatedUseCase).test()

  @Test fun `it emits the initial state for CREATED`() {
    // when
    sourceLifecycleEventsSubject.onNext(CREATED)

    // then
    with(testObserver) {
      assertNoErrors()
      assertValue(initialState)
    }
  }

  @Test fun `it does not emit an state for RESTORED`() {
    // when
    sourceLifecycleEventsSubject.onNext(RESTORED)

    // then
    with(testObserver) {
      assertNoErrors()
      assertNoValues()
    }
  }
}
