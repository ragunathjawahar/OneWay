package io.redgreen.oneway.usecases

import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.SourceEvent
import io.redgreen.oneway.SourceEvent.*
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

  @Test fun `it does not emit an state for RESTORED`() {
    // when
    sourceEventsSubject.onNext(RESTORED)

    // then
    with(testObserver) {
      assertNoErrors()
      assertNoValues()
    }
  }
}
