package io.redgreen.oneway.catalogue.placards.bmi

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import com.jakewharton.rxbinding2.widget.checkedChanges
import com.jakewharton.rxbinding2.widget.itemSelections
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.redgreen.oneway.SourceEvent
import io.redgreen.oneway.catalogue.R
import io.redgreen.oneway.catalogue.base.widget.OneWayConstraintLayout
import io.redgreen.oneway.catalogue.bmi.*
import io.redgreen.oneway.catalogue.bmi.calculator.BmiCategory
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem.SI
import io.redgreen.oneway.catalogue.bmi.drivers.BmiViewDriver
import io.redgreen.oneway.catalogue.bmi.text.BmiTextFormatter
import io.redgreen.oneway.catalogue.bmi.usecases.BmiUseCases
import io.redgreen.oneway.catalogue.placards.bmi.MeasurementAdapter.Quantity.LENGTH
import io.redgreen.oneway.catalogue.placards.bmi.MeasurementAdapter.Quantity.WEIGHT
import kotlinx.android.synthetic.main.bmi_placard.view.*
import kotlin.LazyThreadSafetyMode.NONE

private const val UNINITIALIZED_SELECTION = -1
private const val KEY_WEIGHT_SELECTION = "weight_selection"
private const val KEY_HEIGHT_SELECTION = "height_selection"

class BmiPlacardLayout :
    OneWayConstraintLayout<BmiState>,
    BmiView {
  constructor(
      context: Context
  ) : super(context)

  constructor(
      context: Context,
      attrs: AttributeSet
  ): super(context, attrs)

  constructor(
      context: Context,
      attrs: AttributeSet,
      defStyleAttr: Int
  ): super(context, attrs, defStyleAttr)

  private val minWeightInKg: Int
    get() = resources.getInteger(R.integer.min_weight_kg)

  private val minHeightInCm: Int
    get() = resources.getInteger(R.integer.min_height_cm)

  private val bmiOffset
    get() = BmiOffset(
        minWeightInKg.toDouble(),
        minHeightInCm.toDouble()
    )

  private var weightSelection: Int = UNINITIALIZED_SELECTION
  private var heightSelection: Int = UNINITIALIZED_SELECTION

  private val weightMeasurementAdapter: MeasurementAdapter by lazy(NONE) {
    MeasurementAdapter(context, minWeightInKg, WEIGHT, SI)
  }

  private val heightMeasurementAdapter: MeasurementAdapter by lazy(NONE) {
    MeasurementAdapter(context, minHeightInCm, LENGTH, SI)
  }

  private val intentionsGroup
    get() = BmiIntentionsGroup(
        weightSpinner.itemSelections().skipInitialValue(),
        heightSpinner.itemSelections().skipInitialValue(),
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

  override fun source(
      sourceEvents: Observable<SourceEvent>,
      sourceCopy: Observable<BmiState>
  ): Observable<BmiState> =
      BmiModel.createSource(intentionsGroup.intentions(), sourceEvents, useCases)

  override fun sink(source: Observable<BmiState>): Disposable =
      viewDriver.render(source)

  override fun showBmi(bmi: Double) {
    bmiTextView.text = BmiTextFormatter.getBmiText(context, bmi)
  }

  override fun showCategory(category: BmiCategory) {
    bmiCategoryTextView.text = BmiTextFormatter.getBmiCategoryText(context, category)
  }

  override fun showWeight(weight: Double, measurementSystem: MeasurementSystem) { /* no-op */ }

  override fun showHeight(height: Double, measurementSystem: MeasurementSystem) { /* no-op */ }

  override fun showMeasurementSystem(measurementSystem: MeasurementSystem) {
    measurementSystemSwitch.text = BmiTextFormatter
        .getMeasurementSystemText(context, measurementSystem)

    updateAdapters(measurementSystem)
  }

  override fun onAttachedToWindow() {
    setupAdapters()
    super.onAttachedToWindow()
  }

  override fun onSaveInstanceState(): Parcelable {
    val instanceState = super.onSaveInstanceState() as Bundle
    instanceState.putInt(KEY_WEIGHT_SELECTION, weightSpinner.selectedItemPosition)
    instanceState.putInt(KEY_HEIGHT_SELECTION, heightSpinner.selectedItemPosition)
    return instanceState
  }

  override fun onRestoreInstanceState(state: Parcelable?) {
    if (state == null) {
      return
    }

    val instanceState = state as Bundle
    weightSelection = instanceState[KEY_WEIGHT_SELECTION] as Int
    heightSelection = instanceState[KEY_HEIGHT_SELECTION] as Int
    super.onRestoreInstanceState(state)
  }

  private fun setupAdapters() {
    if (weightSelection == UNINITIALIZED_SELECTION) {
      weightSelection = context.resources.getInteger(R.integer.default_weight_progress)
    }

    if (heightSelection == UNINITIALIZED_SELECTION) {
      heightSelection = context.resources.getInteger(R.integer.default_height_progress)
    }

    weightSpinner.adapter = weightMeasurementAdapter
    heightSpinner.adapter = heightMeasurementAdapter

    weightSpinner.setSelection(weightSelection)
    heightSpinner.setSelection(heightSelection)
  }

  private fun updateAdapters(measurementSystem: MeasurementSystem) {
    weightMeasurementAdapter.measurementSystem = measurementSystem
    heightMeasurementAdapter.measurementSystem = measurementSystem
  }
}
