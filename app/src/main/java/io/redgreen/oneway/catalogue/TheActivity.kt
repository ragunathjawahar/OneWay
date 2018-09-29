package io.redgreen.oneway.catalogue

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dalvik.system.DexFile
import io.redgreen.oneway.android.OneWayFragment
import io.redgreen.oneway.catalogue.showcase.ShowcaseFragment

class TheActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.the_activity)

    if (savedInstanceState == null) {
      supportFragmentManager
          .beginTransaction()
          .replace(R.id.container, ShowcaseFragment())
          .commit()
    }

    DexFile(packageCodePath)
        .entries()
        .toList()
        .forEach {
          try {
            val clazz = Class.forName(it)
            if (OneWayFragment::class.java.isAssignableFrom(clazz)) {
              println(it)
            }
          } catch (e: Throwable) {
            println("$it is pecked up!")
          }
        }
  }
}
