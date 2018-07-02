package io.mobsgeeks.oneway

import com.google.common.truth.Truth.assertThat
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class MviDelegateTest {
  private val publisher = PublishSubject.create<String>()
  private val sourceFunction = { publisher }
  private val testObserver = TestObserver<String>()
  private val sinkFunction: (Observable<String>) -> Disposable = { it -> it.subscribeWith(testObserver) }
  private val mviDelegate = MviDelegate<String>()

  @Test fun `it creates a subscription on setup`() {
    // given
    val theValue = "One Way!"
    val sourceFunction = { Observable.merge(Observable.just(theValue), Observable.never()) }

    // when
    val disposable = mviDelegate.setup(sourceFunction, sinkFunction)

    // then
    testObserver.assertNoErrors()
    testObserver.assertValue(theValue)

    assertThat(disposable.isDisposed)
        .isFalse()
  }

  @Test fun `it disposes the subscription on teardown`() {
    // when
    val disposable = mviDelegate.setup(sourceFunction, sinkFunction)
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
    // when
    val disposable = mviDelegate.setup(sourceFunction, sinkFunction)
    mviDelegate.teardown()
    mviDelegate.teardown()
    mviDelegate.teardown()
    mviDelegate.teardown()

    // then
    assertThat(disposable.isDisposed)
        .isTrue()
  }
}
