package io.mobsgeeks.oneway.android.barebones

import android.support.test.runner.AndroidJUnit4
import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.StateSerializer
import io.mobsgeeks.oneway.android.fixtures.ByteArrayPersister
import io.mobsgeeks.oneway.android.fixtures.ByteArrayStateSerializer
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidMviDelegateTest {
  private val testObserver = TestObserver<String>()
  private val sourceSubject = PublishSubject.create<String>()

  private val androidMviContract = object : AndroidMviContract<String, ByteArray> {
    override val timeline: Observable<String>
      get() = PublishSubject.create()

    override val stateSerializer: StateSerializer<String, ByteArray>
      get() = ByteArrayStateSerializer()

    override val persister: Persister<ByteArray>
      get() = ByteArrayPersister()

    override fun source(
        sourceEvents: Observable<SourceEvent>,
        timeline: Observable<String>
    ): Observable<String> =
        sourceSubject.hide()

    override fun sink(source: Observable<String>): Disposable =
        source.subscribeWith(testObserver)
  }

  private val androidMviDelegate = AndroidMviDelegate(androidMviContract)

  @Test fun bindSubscribesSinkToTheSource() {
    // given
    val testString = "Test 1 2 3"

    // when
    androidMviDelegate.bind()
    sourceSubject.onNext(testString)

    // then
    with(testObserver) {
      assertNoErrors()
      assertValue(testString)
      assertNotTerminated()
    }
  }

  @Test fun unbindUnsubscribesSinkFromTheSource() {
    // given
    androidMviDelegate.bind()

    // when
    androidMviDelegate.unbind()
    sourceSubject.onNext("Allo, can you hear me?")

    // then
    with(testObserver) {
      assertNoErrors()
      assertNoValues()
    }
  }
}
