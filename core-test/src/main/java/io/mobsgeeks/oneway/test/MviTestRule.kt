package io.mobsgeeks.oneway.test

import io.reactivex.subjects.PublishSubject

class MviTestRule<I> {
  val intentions = PublishSubject.create<I>()
}
