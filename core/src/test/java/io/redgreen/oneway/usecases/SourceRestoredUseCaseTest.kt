package io.redgreen.oneway.usecases

import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.SourceLifecycleEvent.CREATED
import io.redgreen.oneway.SourceLifecycleEvent.RESTORED
import org.junit.Test

class SourceRestoredUseCaseTest {
  private val sourceLifecycleEventsSubject = PublishSubject.create<SourceLifecycleEvent>()
  private val sourceCopy = PublishSubject.create<String>()
  private val sourceRestoredUseCase = SourceRestoredUseCase(sourceCopy)
  private val testObserver = sourceLifecycleEventsSubject.compose(sourceRestoredUseCase).test()

  @Test fun `it emits the last known state for RESTORED`() {
    // given
    val lastKnownCat = "Schrodinger's Cat"
    sourceCopy.onNext(lastKnownCat)

    // when
    sourceLifecycleEventsSubject.onNext(RESTORED)

    // then
    with(testObserver) {
      assertNoErrors()
      assertValue(lastKnownCat)
    }
  }

  @Test fun `it does not emit a state for CREATED`() {
    // when
    sourceLifecycleEventsSubject.onNext(CREATED)

    // then
    with(testObserver) {
      assertNoErrors()
      assertNoValues()
    }
  }
}
