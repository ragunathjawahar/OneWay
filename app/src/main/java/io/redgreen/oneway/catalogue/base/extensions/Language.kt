package io.redgreen.oneway.catalogue.base.extensions

fun <T> fastLazy(initializer: () -> T): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE, initializer)
