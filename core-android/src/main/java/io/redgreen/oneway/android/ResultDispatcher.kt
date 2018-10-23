package io.redgreen.oneway.android

import io.reactivex.Maybe

class ResultDispatcher {
  val resultsMap = mutableMapOf<String, Any>()

  inline fun <reified T: Any> setResult(result: T) {
    val key = result::class.java.name
    resultsMap[key] = result as Any
  }

  inline fun <reified T: Any> result(): Maybe<T> { // TODO(rj) 23/Oct/18 - Type inference in return values.
    return Maybe.create<T> { emitter ->
      val key = T::class.java.name
      val result = resultsMap.remove(key)

      if (result != null) {
        emitter.onSuccess(result as T)
      } else {
        emitter.onComplete()
      }
    }
  }
}
