package io.redgreen.oneway.catalogue.bmi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.changes
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.redgreen.oneway.SourceEvent
import io.redgreen.oneway.android.OneWayFragment
import io.redgreen.oneway.catalogue.R
import io.redgreen.oneway.catalogue.bmi.calculator.BmiCategory
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem.SI
import io.redgreen.oneway.catalogue.bmi.drivers.BmiViewDriver
import io.redgreen.oneway.catalogue.bmi.text.BmiTextFormatter
import io.redgreen.oneway.catalogue.bmi.usecases.BmiUseCases
import kotlinx.android.synthetic.main.bmi_fragment.*

class BmiFragment : OneWayFragment<BmiState>(), BmiView {
  private val bmiOffset
    get() = BmiOffset(
        resources.getInteger(R.integer.min_weight_kg).toDouble(),
        resources.getInteger(R.integer.min_height_cm).toDouble()
    )

  private val intentionsGroup
    get() = BmiIntentionsGroup(
        weightSeekBar.changes().skipInitialValue(),
        heightSeekBar.changes().skipInitialValue(),
        measurementSystemSwitch.checkedChanges().skipInitialValue(),
        bmiOffset
    )

  private val initialState
    get() = BmiState(
        resources.getInteger(R.integer.default_weight_kg).toDouble(),
        resources.getInteger(R.integer.default_height_cm).toDouble(),
        SI
    )

  private val useCases
    get() = BmiUseCases(
        initialState,
        sourceCopy
    )

  private val viewDriver
    get() = BmiViewDriver(this)

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View =
      inflater.inflate(R.layout.bmi_fragment, container, false)

  override fun source(
      sourceEvents: Observable<SourceEvent>,
      sourceCopy: Observable<BmiState>
  ): Observable<BmiState> =
    BmiModel.createSource(intentionsGroup.intentions(), sourceEvents, useCases)

  override fun sink(source: Observable<BmiState>): Disposable =
      viewDriver.render(source)

  override fun showBmi(bmi: Double) {
    bmiTextView.text = BmiTextFormatter.getBmiText(context!!, bmi)
  }

  override fun showCategory(category: BmiCategory) {
    bmiCategoryTextView.text = BmiTextFormatter.getBmiCategoryText(context!!, category)
  }

  override fun showWeight(weight: Double, measurementSystem: MeasurementSystem) {
    weightTextView.text = BmiTextFormatter.getWeightText(context!!, weight, measurementSystem)
  }

  override fun showHeight(height: Double, measurementSystem: MeasurementSystem) {
    heightTextView.text = BmiTextFormatter.getHeightText(context!!, height, measurementSystem)
  }

  override fun showMeasurementSystem(measurementSystem: MeasurementSystem) {
    measurementSystemSwitch.text = BmiTextFormatter.getMeasurementSystemText(context!!, measurementSystem)
  }
}
