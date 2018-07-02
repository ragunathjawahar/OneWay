package io.mobsgeeks.oneway

import com.google.common.truth.Truth.assertThat
import io.mobsgeeks.oneway.Binding.CREATED
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
    testObserver.assertNoErrors()
    testObserver.assertValue(theValue)

    assertThat(disposable.isDisposed)
        .isFalse()
  }

  @Test fun `it signals a new binding when the subscription happens for the first time`() {
    // given
    val bindingsTestObserver = TestObserver<Binding>()
    val source: (Observable<Binding>) -> Observable<String> = { bindings ->
      bindings.subscribe(bindingsTestObserver)
      Observable.never<String>()
    }

    // when
    mviDelegate.setup(source, sink)

    // then
    bindingsTestObserver.assertNoErrors()
    bindingsTestObserver.assertValue(CREATED)
    bindingsTestObserver.assertNotTerminated()
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
    testObserver.assertNoErrors()
    testObserver.assertValue(firstMessage)

    assertThat(disposable.isDisposed)
        .isTrue()
  }

  @Test fun `it does not blow up if teardown() is called multiple times`() {
    // given
    val disposable = mviDelegate.setup(source, sink)

    // when
    mviDelegate.teardown()
    mviDelegate.teardown()
    mviDelegate.teardown()
    mviDelegate.teardown()

    // then
    assertThat(disposable.isDisposed)
        .isTrue()
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
}
