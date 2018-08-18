package io.redgreen.oneway.catalogue.bmi

import com.nhaarman.mockito_kotlin.mock
import io.reactivex.subjects.PublishSubject
import org.junit.jupiter.api.Test

class BmiIntentionsGroupTest {
  private val weightChangesSubject = PublishSubject.create<Int>()
  private val heightChangesSubject = PublishSubject.create<Int>()
  private val bmiOffset = BmiOffset(30.0, 120.0)
  private val intentionsGroup = BmiIntentionsGroup(
      weightChangesSubject,
      heightChangesSubject,
      mock(),
      bmiOffset
  )

  private val testObserver = intentionsGroup.intentions().test()

  @Test fun `height changes are offset by min height`() {
    // when
    heightChangesSubject.onNext(0)
    heightChangesSubject.onNext(100)

    // then
    assertIntentions(
        ChangeHeightIntention(0 + bmiOffset.minHeightInCm),
        ChangeHeightIntention(100 + bmiOffset.minHeightInCm)
    )
  }

  @Test fun `weight changes are offset by min weight`() {
    // when
    weightChangesSubject.onNext(0)
    weightChangesSubject.onNext(100)

    // then
    assertIntentions(
        ChangeWeightIntention(0 + bmiOffset.minWeightInKg),
        ChangeWeightIntention(100 + bmiOffset.minWeightInKg)
    )
  }

  private fun assertIntentions(
      intention: BmiIntention,
      vararg intentions: BmiIntention
  ) {
    with(testObserver) {
      assertNoErrors()
      assertValues(intention, *intentions)
      assertNotTerminated()
    }
  }
}
