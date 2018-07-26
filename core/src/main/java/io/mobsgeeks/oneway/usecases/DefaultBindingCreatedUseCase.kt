package io.mobsgeeks.oneway.usecases

import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.Binding.CREATED
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class DefaultBindingCreatedUseCase<T>(
    private val initialState: T
) : ObservableTransformer<Binding, T> {
  override fun apply(bindings: Observable<Binding>): ObservableSource<T> {
    return bindings
        .filter { it == CREATED }
        .map { initialState }
  }
}
