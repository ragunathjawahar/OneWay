package io.redgreen.oneway.android.barebones

import android.os.Bundle
import android.support.test.runner.AndroidJUnit4
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.StateConverter
import io.redgreen.oneway.android.fixtures.ByteArrayPersister
import io.redgreen.oneway.android.fixtures.ByteArrayStateConverter
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class AndroidMviDelegateTest {
  private val testObserver = TestObserver<String>()
  private val sourceSubject = PublishSubject.create<String>()
  private val spiedStateConverter = spy(ByteArrayStateConverter())
  private val spiedPersister = spy(ByteArrayPersister())

  private val androidMviContract = object : AndroidMviContract<String, ByteArray> {
    override val sourceCopy: Observable<String>
      get() = PublishSubject.create() // Unused in tests, hence using a dummy.

    override val stateConverter: StateConverter<String, ByteArray>
      get() = spiedStateConverter

    override val persister: Persister<ByteArray>
      get() = spiedPersister

    override fun source(
        sourceLifecycleEvents: Observable<SourceLifecycleEvent>,
        sourceCopy: Observable<String>
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
  @Test fun saveStateConvertsAndPersistsState() {
    // given
    val preciousState = "My Precious"
    val bundle = Bundle()
    androidMviDelegate.bind()

    // when
    sourceSubject.onNext(preciousState)
    androidMviDelegate.saveState(bundle)

    // then
    val inOrder = inOrder(spiedStateConverter, spiedPersister)
    inOrder.verify(spiedStateConverter).to(preciousState)
    inOrder.verify(spiedPersister).write(preciousState.toByteArray(), bundle)

    verifyNoMoreInteractions(spiedStateConverter)
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
    reset(spiedPersister, spiedStateConverter)

    // when
    androidMviDelegate.restoreState(bundle)

    // then
    val inOrder = inOrder(spiedPersister, spiedStateConverter)
    inOrder.verify(spiedPersister).read(bundle)
    inOrder.verify(spiedStateConverter).from(aState.toByteArray())

    verifyNoMoreInteractions(spiedPersister)
    verifyNoMoreInteractions(spiedStateConverter)
  }
}
