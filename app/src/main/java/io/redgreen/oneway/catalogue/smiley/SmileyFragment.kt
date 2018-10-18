package io.redgreen.oneway.catalogue.smiley

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.android.OneWayFragment
import io.redgreen.oneway.catalogue.R
import io.redgreen.oneway.catalogue.smiley.SmileyPickerActivity.Companion.REQUEST_PICK_SMILEY
import io.redgreen.oneway.catalogue.smiley.usecases.SmileyUseCases
import kotlinx.android.synthetic.main.smiley_fragment.*

class SmileyFragment : OneWayFragment<SmileyState>() {
  private val intentions = PublishSubject.create<SmileyIntention>()

  private val initialState: SmileyState
    get() = SmileyState.initial(getString(R.string.emoji_thumbs_up))

  private var pickedSmiley: String? = null

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View =
      layoutInflater.inflate(R.layout.smiley_fragment, container, false)

  override fun onViewCreated(
      view: View,
      savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    pickSmileyButton.setOnClickListener { SmileyPickerActivity.startForResult(this) }
  }

  override fun source(
      sourceLifecycleEvents: Observable<SourceLifecycleEvent>,
      sourceCopy: Observable<SmileyState>
  ): Observable<SmileyState> =
    SmileyModel.createSource(
        intentions,
        sourceLifecycleEvents,
        SmileyUseCases(initialState, sourceCopy)
    )

  override fun sink(source: Observable<SmileyState>): Disposable =
      source
          .map { it.smiley }
          .subscribe(RxTextView.text(smileyTextView))

  override fun onStart() {
    super.onStart()
    pickedSmiley?.let {
      intentions.onNext(PickSmileyIntention(it))
      pickedSmiley = null
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    val pickSmileySuccessful = requestCode == REQUEST_PICK_SMILEY
        && resultCode == RESULT_OK
        && data != null

    if (pickSmileySuccessful) {
      pickedSmiley = SmileyPickerActivity.getSmiley(data!!)
    }
  }
}
