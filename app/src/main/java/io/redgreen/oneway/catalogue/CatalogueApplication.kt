package io.redgreen.oneway.catalogue

import android.app.Application
import com.squareup.leakcanary.LeakCanary

class CatalogueApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    if (LeakCanary.isInAnalyzerProcess(this)) {
      return
    }
    LeakCanary.install(this)
  }
}
