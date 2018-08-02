package io.mobsgeeks.oneway.usecases

import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.SourceEvent.*
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class DefaultSourceCreatedUseCaseTest {
  private val sourceEventsSubject = PublishSubject.create<SourceEvent>()
  private val initialState = "Idli & Chutney"
  private val defaultSourceCreatedUseCase = DefaultSourceCreatedUseCase(initialState)
  private val testObserver = sourceEventsSubject.compose(defaultSourceCreatedUseCase).test()

  @Test fun `it does nothing for DESTROYED and RESTORED events`() {
    // when
    sourceEventsSubject.onNext(DESTROYED)
    sourceEventsSubject.onNext(RESTORED)

    // then
    with(testObserver) {
      assertNoErrors()
      assertNoValues()
    }
  }

  @Test fun `it emits an initial state for CREATED event`() {
    // when
    sourceEventsSubject.onNext(CREATED)

    // then
    with(testObserver) {
      assertNoErrors()
      assertValue(initialState)
    }
  }
}
