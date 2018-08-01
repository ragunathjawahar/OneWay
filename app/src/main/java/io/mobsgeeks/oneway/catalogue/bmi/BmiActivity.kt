package io.mobsgeeks.oneway.catalogue.bmi

import android.os.Bundle
import com.jakewharton.rxbinding2.widget.changes
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.catalogue.R
import io.mobsgeeks.oneway.catalogue.bmi.usecases.BmiUseCases
import io.mobsgeeks.oneway.catalogue.bmi.usecases.ChangeHeightUseCase
import io.mobsgeeks.oneway.catalogue.bmi.usecases.ChangeMeasurementSystemUseCase
import io.mobsgeeks.oneway.catalogue.bmi.usecases.ChangeWeightUseCase
import io.mobsgeeks.oneway.catalogue.mvi.MviActivity
import io.mobsgeeks.oneway.usecases.DefaultBindingCreatedUseCase
import io.mobsgeeks.oneway.usecases.DefaultBindingRestoredUseCase
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.bmi_fragment.*

class BmiActivity : MviActivity<BmiState>() {
  private val intentions
    get() = BmiIntentions(
        weightSeekBar.changes().skipInitialValue(),
        heightSeekBar.changes().skipInitialValue(),
        measurementSystemSwitch.checkedChanges().skipInitialValue()
    )

  private val useCases
    get() = BmiUseCases(
        DefaultBindingCreatedUseCase(BmiState.INITIAL),
        DefaultBindingRestoredUseCase(timeline),
        ChangeWeightUseCase(timeline),
        ChangeHeightUseCase(timeline),
        ChangeMeasurementSystemUseCase(timeline)
    )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.bmi_fragment)
  }

  override fun source(
      bindings: Observable<Binding>,
      timeline: Observable<BmiState>
  ): Observable<BmiState> =
    BmiModel.bind(intentions.stream(), bindings, useCases)

  override fun sink(source: Observable<BmiState>): Disposable {
    TODO("not implemented")
  }
}
