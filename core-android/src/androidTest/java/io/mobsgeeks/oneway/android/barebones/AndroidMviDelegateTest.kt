package io.mobsgeeks.oneway.android.barebones

import android.os.Bundle
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
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class AndroidMviDelegateTest {
  private val testObserver = TestObserver<String>()
  private val sourceSubject = PublishSubject.create<String>()
  private val spiedStateSerializer = spy(ByteArrayStateSerializer())
  private val spiedPersister = spy(ByteArrayPersister())

  private val androidMviContract = object : AndroidMviContract<String, ByteArray> {
    override val timeline: Observable<String>
      get() = PublishSubject.create() // Unused in tests, hence using a dummy.

    override val stateSerializer: StateSerializer<String, ByteArray>
      get() = spiedStateSerializer

    override val persister: Persister<ByteArray>
      get() = spiedPersister

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

  // FIXME(rj) 5/Aug/18 - Tests internal implementation, hence a tightly-coupled test.
  @Test fun saveStateSerializesAndPersistsState() {
    // given
    val preciousState = "My Precious"
    val bundle = Bundle()
    androidMviDelegate.bind()

    // when
    sourceSubject.onNext(preciousState)
    androidMviDelegate.saveState(bundle)

    // then
    val inOrder = inOrder(spiedStateSerializer, spiedPersister)
    inOrder.verify(spiedStateSerializer).serialize(preciousState)
    inOrder.verify(spiedPersister).write(preciousState.toByteArray(), bundle)

    verifyNoMoreInteractions(spiedStateSerializer)
    verifyNoMoreInteractions(spiedPersister)
  }

  // FIXME(rj) 5/Aug/18 - Tests internal implementation, hence a tightly-coupled test.
  @Test fun restoreStateRestoresSavedState() {
    // given
    val aState = "Tamil Nadu"
    val bundle = Bundle()
    androidMviDelegate.bind()
    sourceSubject.onNext(aState)
    androidMviDelegate.saveState(bundle)
    reset(spiedPersister, spiedStateSerializer)

    // when
    androidMviDelegate.restoreState(bundle)

    // then
    val inOrder = inOrder(spiedPersister, spiedStateSerializer)
    inOrder.verify(spiedPersister).read(bundle)
    inOrder.verify(spiedStateSerializer).deserialize(aState.toByteArray())

    verifyNoMoreInteractions(spiedPersister)
    verifyNoMoreInteractions(spiedStateSerializer)
  }
}
