package io.redgreen.oneway.catalogue

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.redgreen.oneway.catalogue.signup.SignUpFragment

class TheActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.the_activity)

    if (savedInstanceState == null) {
      supportFragmentManager
          .beginTransaction()
          .replace(R.id.container, SignUpFragment())
          .commit()
    }
  }
}
