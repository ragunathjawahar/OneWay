package io.mobsgeeks.oneway.catalogue.budapest

import android.os.Bundle
import com.jakewharton.rxbinding2.widget.textChanges
import io.mobsgeeks.oneway.SourceEvent
import io.mobsgeeks.oneway.catalogue.R
import io.mobsgeeks.oneway.catalogue.budapest.drivers.BudapestViewDriver
import io.mobsgeeks.oneway.catalogue.budapest.usecases.BudapestUseCases
import io.mobsgeeks.oneway.catalogue.budapest.usecases.NameChangeUseCase
import io.mobsgeeks.oneway.catalogue.mvi.MviActivity
import io.mobsgeeks.oneway.usecases.DefaultSourceCreatedUseCase
import io.mobsgeeks.oneway.usecases.DefaultSourceRestoredUseCase
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.budapest_fragment.*

class BudapestActivity : MviActivity<BudapestState>(), BudapestView {
  private val intentions: Observable<BudapestIntention>
    get() = nameEditText.textChanges().skipInitialValue()
        .map { it.toString() }
        .map { NameChangeIntention(it) }

  private val useCases: BudapestUseCases
    get() = BudapestUseCases(
        DefaultSourceCreatedUseCase(BudapestState.STRANGER),
        DefaultSourceRestoredUseCase(timeline),
        NameChangeUseCase()
    )

  private val viewDriver: BudapestViewDriver
    get() = BudapestViewDriver(this)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.budapest_fragment)
  }

  override fun source(
      sourceEvents: Observable<SourceEvent>,
      timeline: Observable<BudapestState>
  ): Observable<BudapestState> =
      BudapestModel.createSource(intentions, sourceEvents, useCases)

  override fun sink(source: Observable<BudapestState>): Disposable =
      viewDriver.render(source)

  override fun greetStranger() {
    greetingTextView.text = getString(R.string.hello_stranger)
  }

  override fun greet(name: String) {
    greetingTextView.text = getString(R.string.template_greeting, name)
  }
}
