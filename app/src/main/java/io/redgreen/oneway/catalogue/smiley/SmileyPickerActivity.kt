package io.redgreen.oneway.catalogue.smiley

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import io.redgreen.oneway.catalogue.R
import kotlinx.android.synthetic.main.smiley_picker_activity.*

class SmileyPickerActivity : AppCompatActivity() {
  companion object {
    const val REQUEST_PICK_SMILEY = 0x007 // NOT James Bond in hex.
    private const val KEY_SMILEY = "smiley"

    fun startForResult(fragment: Fragment) {
      val intent = Intent(fragment.activity, SmileyPickerActivity::class.java)
      fragment.startActivityForResult(intent, REQUEST_PICK_SMILEY)
    }

    fun getSmiley(data: Intent): String =
        data.getStringExtra(KEY_SMILEY)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.smiley_picker_activity)

    rollingEyesButton.setOnClickListener { pickEmoji(R.string.emoji_rolling_eyes) }
    victoryHandButton.setOnClickListener { pickEmoji(R.string.emoji_victory_hand) }
  }

  private fun pickEmoji(@StringRes emoji: Int) {
    val data = Intent().apply { putExtra(KEY_SMILEY, getString(emoji)) }
    setResult(Activity.RESULT_OK, data)
    finish()
  }
}
