package io.mobsgeeks.oneway.catalogue.base

import io.reactivex.Scheduler

interface SchedulersProvider {
  fun computation(): Scheduler
}
