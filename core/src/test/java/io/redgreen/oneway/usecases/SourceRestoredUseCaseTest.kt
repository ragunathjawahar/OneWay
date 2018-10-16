package io.redgreen.oneway.usecases

import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.SourceEvent
import io.redgreen.oneway.SourceEvent.*
import org.junit.Test

class SourceRestoredUseCaseTest {
  private val sourceEventsSubject = PublishSubject.create<SourceEvent>()
  private val sourceCopy = PublishSubject.create<String>()
  private val sourceRestoredUseCase = SourceRestoredUseCase(sourceCopy)
  private val testObserver = sourceEventsSubject.compose(sourceRestoredUseCase).test()

  @Test fun `it emits the last known state for RESTORED`() {
    // given
    val lastKnownCat = "Schrodinger's Cat"
    sourceCopy.onNext(lastKnownCat)

    // when
    sourceEventsSubject.onNext(RESTORED)

    // then
    with(testObserver) {
      assertNoErrors()
      assertValue(lastKnownCat)
    }
  }

  @Test fun `it does nothing for CREATED and DESTROYED`() {
    // when
    sourceEventsSubject.onNext(CREATED)
    sourceEventsSubject.onNext(DESTROYED)

    // then
    with(testObserver) {
      assertNoErrors()
      assertNoValues()
    }
  }
}
