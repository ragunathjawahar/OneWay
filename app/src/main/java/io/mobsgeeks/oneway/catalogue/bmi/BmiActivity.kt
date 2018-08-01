package io.mobsgeeks.oneway.catalogue.bmi

import android.os.Bundle
import android.support.annotation.StringRes
import com.jakewharton.rxbinding2.widget.changes
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.mobsgeeks.oneway.Binding
import io.mobsgeeks.oneway.catalogue.R
import io.mobsgeeks.oneway.catalogue.bmi.MeasurementSystem.IMPERIAL
import io.mobsgeeks.oneway.catalogue.bmi.MeasurementSystem.SI
import io.mobsgeeks.oneway.catalogue.bmi.drivers.BmiViewDriver
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

class BmiActivity : MviActivity<BmiState>(), BmiView {
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

  private val viewDriver
    get() = BmiViewDriver(this)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.bmi_fragment)
  }

  override fun source(
      bindings: Observable<Binding>,
      timeline: Observable<BmiState>
  ): Observable<BmiState> =
    BmiModel.bind(intentions.stream(), bindings, useCases)

  override fun sink(source: Observable<BmiState>): Disposable =
      viewDriver.render(source)

  override fun showBmi(bmi: Double) {
    bmiTextView.text = getString(R.string.template_bmi, bmi)
  }

  override fun showCategory(category: BmiCategory) {
    bmiCategoryTextView.text = category.toString()
  }

  override fun showWeight(weight: Double, measurementSystem: MeasurementSystem) {
    @StringRes val weightTemplateStringRes = when (measurementSystem) {
      SI       -> R.string.template_weight_si
      IMPERIAL -> R.string.template_weight_imperial
    }
    weightTextView.text = getString(weightTemplateStringRes, weight)
  }

  override fun showHeight(height: Double, measurementSystem: MeasurementSystem) {
    @StringRes val heightTemplateStringRes = when (measurementSystem) {
      SI       -> R.string.template_height_si
      IMPERIAL -> R.string.template_height_imperial
    }
    heightTextView.text = getString(heightTemplateStringRes, height)
  }

  override fun showMeasurementSystem(measurementSystem: MeasurementSystem) {
    @StringRes val measurementSystemRes = when (measurementSystem) {
      SI       -> R.string.si_units
      IMPERIAL -> R.string.imperial_units
    }
    measurementSystemSwitch.text = getString(measurementSystemRes)
  }
}
