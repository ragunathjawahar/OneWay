package io.mobsgeeks.oneway.usecases

import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.SourceEvent.*
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class DefaultSourceRestoredUseCaseTest {
  private val sourceEventsSubject = PublishSubject.create<SourceEvent>()
  private val timeline = PublishSubject.create<String>()
  private val defaultSourceRestoredUseCase = DefaultSourceRestoredUseCase(timeline)
  private val testObserver = sourceEventsSubject.compose(defaultSourceRestoredUseCase).test()

  @Test fun `it does nothing for CREATED and DESTROYED events`() {
    // when
    sourceEventsSubject.onNext(CREATED)
    sourceEventsSubject.onNext(DESTROYED)

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
    sourceEventsSubject.onNext(RESTORED)

    // then
    with(testObserver) {
      assertNoErrors()
      assertValue(lastKnownCat)
    }
  }
}
