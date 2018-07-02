package io.mobsgeeks.oneway

import com.google.common.truth.Truth.assertThat
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.TestObserver
import org.junit.Test

class MviDelegateTest {
  @Test fun `it creates a subscription on setup`() {
    // given
    val theValue = "One Way!"
    val sourceFunction = { Observable.merge(Observable.just(theValue), Observable.never()) }
    val testObserver = TestObserver<String>()
    val sinkFunction: (Observable<String>) -> Disposable = { it -> it.subscribeWith(testObserver) }
    val mviDelegate = MviDelegate<String>()

    // when
    val disposable = mviDelegate.setup(sourceFunction, sinkFunction)

    // then
    testObserver.assertNoErrors()
    testObserver.assertValue(theValue)

    assertThat(disposable.isDisposed)
        .isFalse()
  }
}
