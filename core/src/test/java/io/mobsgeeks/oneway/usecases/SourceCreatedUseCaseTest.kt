package io.mobsgeeks.oneway.usecases

import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.SourceEvent.*
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class SourceCreatedUseCaseTest {
  private val sourceEventsSubject = PublishSubject.create<SourceEvent>()
  private val initialState = "Idli & Chutney"
  private val sourceCreatedUseCase = SourceCreatedUseCase(initialState)
  private val testObserver = sourceEventsSubject.compose(sourceCreatedUseCase).test()

  @Test fun `it emits the initial state for CREATED`() {
    // when
    sourceEventsSubject.onNext(CREATED)

    // then
    with(testObserver) {
      assertNoErrors()
      assertValue(initialState)
    }
  }

  @Test fun `it does nothing for DESTROYED and RESTORED`() {
    // when
    sourceEventsSubject.onNext(DESTROYED)
    sourceEventsSubject.onNext(RESTORED)

    // then
    with(testObserver) {
      assertNoErrors()
      assertNoValues()
    }
  }
}
