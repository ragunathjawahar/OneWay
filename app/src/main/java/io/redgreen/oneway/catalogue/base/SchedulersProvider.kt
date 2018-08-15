package io.redgreen.oneway.catalogue.base

import io.reactivex.Scheduler

interface SchedulersProvider {
  fun ui(): Scheduler
  fun computation(): Scheduler
}
