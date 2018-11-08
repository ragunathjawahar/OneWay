package io.redgreen.oneway.catalogue.budapest

import io.reactivex.subjects.PublishSubject
import org.junit.jupiter.api.Test

class BudapestIntentionsTest {
  private val nameTextChangesSubject = PublishSubject.create<CharSequence>()
  private val budapestIntentions = BudapestIntentions(nameTextChangesSubject)
  private val intentionTestObserver = budapestIntentions.stream().test()

  @Test fun `when name is non-blank, then emit enter name intention`() {
    // when
    val someName = "Joe"
    nameTextChangesSubject.onNext(someName)

    // then
    assertIntentions(EnterNameIntention(someName))
  }

  @Test fun `when name is empty, then emit no name intention`() {
    // when
    nameTextChangesSubject.onNext("")

    // then
    assertIntentions(NoNameIntention)
  }

  @Test fun `when name is blank, then emit no name intention`() {
    // when
    nameTextChangesSubject.onNext("          ")

    // then
    assertIntentions(NoNameIntention)
  }

  private fun assertIntentions(vararg intentions: BudapestIntention) {
    with(intentionTestObserver) {
      assertNoErrors()
      assertValues(*intentions)
      assertNotTerminated()
    }
  }
}
