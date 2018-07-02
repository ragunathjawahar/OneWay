package io.mobsgeeks.oneway

import io.reactivex.disposables.Disposable

internal class ReadOnlyDisposable(private val disposable: Disposable) : Disposable {
  override fun dispose() {
    throw IllegalStateException("Sorry :( this disposable is read-only.")
  }

  override fun isDisposed(): Boolean =
      disposable.isDisposed
}
