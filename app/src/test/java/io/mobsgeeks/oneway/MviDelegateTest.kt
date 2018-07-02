package io.mobsgeeks.oneway

import io.mobsgeeks.oneway.Binding.CREATED
import io.mobsgeeks.oneway.Binding.DESTROYED
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class MviDelegateTest {
  private val publisher = PublishSubject.create<String>()
  private val source: (Observable<Binding>) -> Observable<String> = { _ -> publisher }
  private val testObserver = TestObserver<String>()
  private val sink: (Observable<String>) -> Disposable = { it -> it.subscribeWith(testObserver) }
  private val mviDelegate = MviDelegate<String>()

  @Test fun `it creates a subscription on setup`() {
    // given
    val theValue = "One Way!"
    val source: (Observable<Binding>) -> Observable<String> = { _ ->
      Observable.merge(Observable.just(theValue), Observable.never())
    }

    // when
    mviDelegate.setup(source, sink)

    // then
    with(testObserver) {
      assertNoErrors()
      assertValue(theValue)
    }
  }

  @Test fun `it signals a new binding when the subscription happens for the first time`() {
    // given
    val bindingsTestObserver = TestObserver<Binding>()

    // when
    mviDelegate.bindings.subscribe(bindingsTestObserver)
    mviDelegate.setup(source, sink)

    // then
    with(bindingsTestObserver) {
      assertNoErrors()
      assertValue(CREATED)
      assertNotTerminated()
    }
  }

  @Test fun `it signals a destroyed binding on teardown()`() {
    // given
    val bindingsTestObserver = TestObserver<Binding>()

    // when
    mviDelegate.bindings.subscribe(bindingsTestObserver)
    mviDelegate.setup(source, sink)
    mviDelegate.teardown()

    // then
    with(bindingsTestObserver) {
      assertNoErrors()
      assertValues(CREATED, DESTROYED)
      assertNotTerminated()
    }
  }

  @Test fun `it has a timeline that provides access to the latest state`() {
    // given
    val timelineTestObserver = TestObserver<String>()

    // when
    mviDelegate.timeline.subscribe(timelineTestObserver)
    mviDelegate.setup(source, sink)
    publisher.onNext("ஒன்று")
    publisher.onNext("இரண்டு")
    publisher.onNext("மூன்று")

    // then
    with(timelineTestObserver) {
      assertNoErrors()
      assertValues("ஒன்று", "இரண்டு", "மூன்று")
      assertNotTerminated()
    }

    with(testObserver) {
      assertNoErrors()
      assertValues("ஒன்று", "இரண்டு", "மூன்று")
      assertNotTerminated()
    }
  }

  @Test fun `it disposes the subscription on teardown`() {
    // given
    mviDelegate.setup(source, sink)

    // when
    val firstMessage = "Hello"
    publisher.onNext(firstMessage)

    mviDelegate.teardown()
    publisher.onNext("Unreachable")

    // then
    with(testObserver) {
      assertNoErrors()
      assertValue(firstMessage)
    }
  }

  @Test fun `it does not blow up if teardown() is called multiple times`() {
    // given
    mviDelegate.setup(source, sink)

    // when
    with(mviDelegate) {
      teardown()
      teardown()
      teardown()
      teardown()
    }
  }

  @Test fun `it does not blow up if teardown() is called before setup`() {
    // given
    val bindingsTestObserver = TestObserver<Binding>()
    mviDelegate.bindings.subscribe(bindingsTestObserver)

    // when
    mviDelegate.teardown()

    // then
    with(bindingsTestObserver) {
      assertNoErrors()
      assertNoValues()
      assertNotTerminated()
    }
  }
}
