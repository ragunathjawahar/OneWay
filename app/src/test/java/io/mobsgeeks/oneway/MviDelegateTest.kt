package io.mobsgeeks.oneway

import com.google.common.truth.Truth.assertThat
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
    val disposable = mviDelegate.setup(source, sink)

    // then
    with(testObserver) {
      assertNoErrors()
      assertValue(theValue)
    }

    assertThat(disposable.isDisposed)
        .isFalse()
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

  @Test fun `it signals a destroyed binding when the subscription is torn down`() {
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

  @Test fun `it has a timeline that always has access to the latest state`() {
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
    val disposable = mviDelegate.setup(source, sink)

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

    assertThat(disposable.isDisposed)
        .isTrue()
  }

  @Test fun `it does not blow up if teardown() is called multiple times`() {
    // given
    val disposable = mviDelegate.setup(source, sink)

    // when
    with(mviDelegate) {
      teardown()
      teardown()
      teardown()
      teardown()
    }

    // then
    assertThat(disposable.isDisposed)
        .isTrue()
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

  @Test fun `it returns a disposable that is read-only`() {
    // given
    val disposable = mviDelegate.setup(source, sink)

    // when
    try {
      disposable.dispose()
    } catch (e: IllegalStateException) {
      // swallow... abuk abuk
    }

    // then
    assertThat(disposable.isDisposed)
        .isFalse()
  }

  // 1. Bindings -> RESTORED
  // 2. Ensure that a state is constantly fed into the relay.
  // 3. Save and restore state (use a persist)
  // 4. Timeline MVI Delegate
  // 5. Lift the UI and replay states when building UI.
}
