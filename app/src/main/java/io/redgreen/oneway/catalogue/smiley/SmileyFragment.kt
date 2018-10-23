package io.redgreen.oneway.catalogue.smiley

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import arrow.core.None
import arrow.core.Some
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.android.OneWayFragment
import io.redgreen.oneway.android.ResultDispatcher
import io.redgreen.oneway.catalogue.R
import io.redgreen.oneway.catalogue.smiley.SmileyPickerActivity.Companion.REQUEST_PICK_SMILEY
import io.redgreen.oneway.catalogue.smiley.usecases.SmileyUseCases
import kotlinx.android.synthetic.main.smiley_fragment.*

class SmileyFragment : OneWayFragment<SmileyState>() {
  companion object {
    private const val SMILEY_NONE = ""
  }

  private val intentions = PublishSubject.create<SmileyIntention>()

  private val initialState: SmileyState
    get() = SmileyState.initial(getString(R.string.emoji_thumbs_up))

  private val resultDispatcher = ResultDispatcher()
  private val compositeDisposable = CompositeDisposable()

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
          .map { it.smiley.character }
          .subscribe(RxTextView.text(smileyTextView))

  override fun onStart() {
    super.onStart()
    compositeDisposable += resultDispatcher
        .result<String>()
        .map { if (it != SMILEY_NONE) PickSmileyIntention(Some(it)) else PickSmileyIntention(None) }
        .subscribe { intentions.onNext(it) }
  }

  override fun onStop() {
    super.onStop()
    compositeDisposable.clear()
  }

  override fun onActivityResult(
      requestCode: Int,
      resultCode: Int,
      data: Intent?
  ) {
    if (requestCode == REQUEST_PICK_SMILEY) {
      val smileyResult = if (resultCode == RESULT_OK) {
        SmileyPickerActivity.getSmiley(data!!)
      } else {
        SMILEY_NONE
      }
      resultDispatcher.setResult(smileyResult)
    }
  }
}
