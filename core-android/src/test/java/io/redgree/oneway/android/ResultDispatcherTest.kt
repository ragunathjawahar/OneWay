package io.redgree.oneway.android

import io.reactivex.observers.TestObserver
import io.redgreen.oneway.android.ResultDispatcher
import org.junit.jupiter.api.Test

class ResultDispatcherTest {
  private val resultDispatcher = ResultDispatcher()
  private val cat = Cat("Billy")

  @Test fun `it can dispatch a result when subscribed to`() {
    // given
    resultDispatcher.setResult(cat)

    // when
    val resultTestObserver = resultDispatcher.result<Cat>().test()

    // then
    assertValue(resultTestObserver, cat)
  }

  @Test fun `it clears the result after first emission`() {
    // given
    resultDispatcher.setResult(cat)
    val firstResultTestObserver = TestObserver<Any>()
    val secondResultTestObserver = TestObserver<Any>()

    // when
    resultDispatcher.result<Cat>().subscribe(firstResultTestObserver)
    resultDispatcher.result<Cat>().subscribe(secondResultTestObserver)

    // then
    assertValue(firstResultTestObserver, cat)
    assertNoValue(secondResultTestObserver)
  }

  @Test fun `it can handle different types of results`() {
    // given
    val dog = Dog("Oreo")
    resultDispatcher.setResult(dog)
    resultDispatcher.setResult(cat)

    val dogTestObserver = TestObserver<Dog>()
    val catTestObserver = TestObserver<Cat>()

    // when
    resultDispatcher.result<Dog>().subscribe(dogTestObserver)
    resultDispatcher.result<Cat>().subscribe(catTestObserver)

    // then
    assertValue(dogTestObserver, dog)
    assertValue(catTestObserver, cat)
  }

  @Test fun `it does not dispatch results to types it does not have results for`() {
    // given
    val unitTestObserver = TestObserver<Unit>()

    // when
    resultDispatcher.result<Unit>().subscribe(unitTestObserver)

    // then
    assertNoValue(unitTestObserver)
  }

  private fun <T> assertValue(testObserver: TestObserver<T>, value: T) {
    with(testObserver) {
      assertNoErrors()
      assertValue(value)
      assertTerminated()
    }
  }

  private fun <T> assertNoValue(testObserver: TestObserver<T>) {
    with(testObserver) {
      assertNoErrors()
      assertNoValues()
      assertTerminated()
    }
  }
}

data class Cat(
    val name: String
)

data class Dog(
    val name: String
)
