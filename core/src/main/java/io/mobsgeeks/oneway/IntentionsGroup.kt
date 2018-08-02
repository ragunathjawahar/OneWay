package io.mobsgeeks.oneway

import io.reactivex.Observable

interface IntentionsGroup<I> {
  fun intentions(): Observable<I>
}
