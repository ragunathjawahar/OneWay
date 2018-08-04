package io.mobsgeeks.oneway

import com.google.common.truth.Truth.assertThat
import io.mobsgeeks.oneway.SourceEvent.*
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class MviDelegateTest {
  private val publisher = PublishSubject.create<String>()

  private val source = object : Source<String> {
    override fun produce(
        sourceEvents: Observable<SourceEvent>,
        timeline: Observable<String>
    ): Observable<String> =
        publisher
  }

  private val testObserver = TestObserver<String>()
  private val sourceEventsTestObserver = TestObserver<SourceEvent>()

  private val sink = object : Sink<String> {
    override fun consume(source: Observable<String>): Disposable =
        source.subscribeWith(testObserver)
  }

  private val stateSerializer: StateSerializer<String, ByteArray> = object : StateSerializer<String, ByteArray> {
    override fun serialize(state: String): ByteArray =
        state.toByteArray()

    override fun deserialize(persistableState: ByteArray): String =
        String(persistableState)
  }

  private val mviDelegate = MviDelegate(stateSerializer)

  @Test fun `it creates a subscription on bind`() {
    // given
    val theValue = "One Way!"
    val source = object : Source<String> {
      override fun produce(
          sourceEvents: Observable<SourceEvent>,
          timeline: Observable<String>
      ): Observable<String> =
          Observable.merge(Observable.just(theValue), Observable.never())
    }

    // when
    mviDelegate.bind(source, sink)

    // then
    with(testObserver) {
      assertNoErrors()
      assertValue(theValue)
    }
  }

  @Test fun `it signals a CREATED source event when the subscription happens for the first time`() {
    // when
    mviDelegate.sourceEvents.subscribe(sourceEventsTestObserver)
    mviDelegate.bind(source, sink)

    // then
    with(sourceEventsTestObserver) {
      assertNoErrors()
      assertValue(CREATED)
      assertNotTerminated()
    }

    assertThat(sourceEventsTestObserver.hasSubscription())
        .isTrue()
  }

  @Test fun `it signals a DESTROYED source event on unbind`() {
    // given
    mviDelegate.sourceEvents.subscribe(sourceEventsTestObserver)

    // when
    mviDelegate.bind(source, sink)
    mviDelegate.unbind()

    // then
    with(sourceEventsTestObserver) {
      assertNoErrors()
      assertValues(CREATED, DESTROYED)
      assertNotTerminated()
    }
  }

  @Test fun `it has a timeline that provides access to the latest state`() {
    // given
    val timelineTestObserver = TestObserver<String>()
    val events = arrayOf("ஒன்று", "இரண்டு", "மூன்று")

    // when
    mviDelegate.timeline.subscribe(timelineTestObserver)
    mviDelegate.bind(source, sink)
    events.forEach { publisher.onNext(it) }

    // then
    with(timelineTestObserver) {
      assertNoErrors()
      assertValues(*events)
      assertNotTerminated()
    }

    with(testObserver) {
      assertNoErrors()
      assertValues(*events)
      assertNotTerminated()
    }
  }

  @Test fun `it returns null if a last known state is unavailable`() {
    // given
    mviDelegate.bind(source, sink)

    // when
    val lastKnownState = mviDelegate.saveState()

    // then
    assertThat(lastKnownState)
        .isNull()
  }

  @Test fun `it returns the last known state in desired format when available`() {
    // given
    val arrow = "Arrow"
    val persistableArrow = arrow.toByteArray()
    mviDelegate.bind(source, sink)

    // when
    publisher.onNext(arrow)
    val lastKnownState = mviDelegate.saveState()

    // then
    assertThat(lastKnownState)
        .isNotNull()
    assertThat(lastKnownState)
        .isEqualTo(persistableArrow)
  }

  @Test fun `it can restore state when a last known state is available`() {
    // given
    val arrow = "Arrow"
    val persistentState = arrow.toByteArray()
    val timelineTestObserver = TestObserver<String>()

    // when
    mviDelegate.timeline.subscribe(timelineTestObserver)
    mviDelegate.restoreState(persistentState)
    val restoredState = mviDelegate.saveState()

    // then
    with(timelineTestObserver) {
      assertNoErrors()
      assertValue(arrow)
      assertNotTerminated()
    }

    assertThat(restoredState)
        .isEqualTo(persistentState)
  }

  @Test fun `it signals a RESTORED source event when state is restored`() {
    // given
    val persistentState = "Arrow".toByteArray()

    // when
    mviDelegate.sourceEvents.subscribe(sourceEventsTestObserver)
    mviDelegate.restoreState(persistentState)
    mviDelegate.bind(source, sink)

    // then
    with(sourceEventsTestObserver) {
      assertNoErrors()
      assertValue(RESTORED)
      assertNotTerminated()
    }
  }

  @Test fun `it signals a CREATED source event if the restored state is null`() {
    // given
    mviDelegate.sourceEvents.subscribe(sourceEventsTestObserver)

    // when
    mviDelegate.bind(source, sink)
    mviDelegate.unbind()
    mviDelegate.restoreState(null)
    mviDelegate.bind(source, sink)

    // then
    with(sourceEventsTestObserver) {
      assertNoErrors()
      assertValues(CREATED, DESTROYED, CREATED)
      assertNotTerminated()
    }
  }

  @Test fun `it disposes the subscription on unbind`() {
    // given
    mviDelegate.bind(source, sink)

    // when
    val firstMessage = "Hello"
    publisher.onNext(firstMessage)

    mviDelegate.unbind()
    publisher.onNext("Unreachable")

    // then
    with(testObserver) {
      assertNoErrors()
      assertValue(firstMessage)
    }

    assertThat(testObserver.isDisposed)
        .isTrue()
  }

  @Test fun `it does not blow up if unbind is called multiple times`() {
    // given
    mviDelegate.bind(source, sink)

    // when
    with(mviDelegate) {
      unbind()
      unbind()
      unbind()
      unbind()
    }
  }

  @Test fun `it does not blow up if unbind is called before bind`() {
    // given
    mviDelegate.sourceEvents.subscribe(sourceEventsTestObserver)

    // when
    mviDelegate.unbind()

    // then
    with(sourceEventsTestObserver) {
      assertNoErrors()
      assertNoValues()
      assertNotTerminated()
    }
  }
}
