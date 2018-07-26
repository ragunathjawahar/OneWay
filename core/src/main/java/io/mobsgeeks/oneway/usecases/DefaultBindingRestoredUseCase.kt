package io.mobsgeeks.oneway.usecases

import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.Binding.RESTORED
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction

class DefaultBindingRestoredUseCase<T>(
    private val timeline: Observable<T>
) : ObservableTransformer<Binding, T> {
  override fun apply(bindings: Observable<Binding>): Observable<T> {
    return bindings
        .filter { it == RESTORED }
        .withLatestFrom(timeline, BiFunction { _, state -> state })
  }
}
