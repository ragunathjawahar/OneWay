package io.redgreen.oneway.catalogue.placards.bmi

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import io.redgreen.oneway.catalogue.bmi.calculator.ImperialUnitConverter
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem.IMPERIAL
import io.redgreen.oneway.catalogue.bmi.text.BmiTextFormatter
import io.redgreen.oneway.catalogue.placards.bmi.MeasurementAdapter.Quantity.LENGTH
import io.redgreen.oneway.catalogue.placards.bmi.MeasurementAdapter.Quantity.WEIGHT

@LayoutRes private const val ITEM_VIEW = android.R.layout.simple_spinner_item
@LayoutRes private const val ITEM_VIEW_DROPDOWN = android.R.layout.simple_dropdown_item_1line
private const val ITEM_COUNT = 101

class MeasurementAdapter(
    context: Context,
    private val startValue: Int,
    private val quantity: Quantity,
    theMeasurementSystem: MeasurementSystem
) : ArrayAdapter<Int>(context, ITEM_VIEW) {
  var measurementSystem: MeasurementSystem = theMeasurementSystem
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  private val layoutInflater = LayoutInflater.from(context)

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
      createRow(ITEM_VIEW, convertView, parent, position)

  override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View =
      createRow(ITEM_VIEW_DROPDOWN, convertView, parent, position)

  override fun getCount(): Int =
      ITEM_COUNT

  private fun createRow(
      @LayoutRes layoutResId: Int,
      convertView: View?,
      parent: ViewGroup?,
      position: Int
  ): TextView {
    val rowItemView = (convertView ?: layoutInflater.inflate(layoutResId, parent, false))
        as TextView

    val siQuantity = (startValue + position).toDouble()
    val quantityToDisplay = getQuantityToDisplay(siQuantity, measurementSystem)

    rowItemView.text = getDisplayText(rowItemView.context, quantityToDisplay, measurementSystem)

    return rowItemView
  }

  private fun getQuantityToDisplay(
      siQuantity: Double,
      measurementSystem: MeasurementSystem
  ): Double = if (measurementSystem == IMPERIAL) {
    when (quantity) {
      WEIGHT -> ImperialUnitConverter.toPounds(siQuantity)
      LENGTH -> ImperialUnitConverter.toFoot(siQuantity)
    }
  } else {
    siQuantity
  }

  private fun getDisplayText(
      context: Context,
      quantityToDisplay: Double,
      measurementSystem: MeasurementSystem
  ): String {
    return when (quantity) {
      WEIGHT -> BmiTextFormatter.getWeightText(context, quantityToDisplay, measurementSystem)
      LENGTH -> BmiTextFormatter.getHeightText(context, quantityToDisplay, measurementSystem)
    }
  }

  enum class Quantity {
    LENGTH, WEIGHT
  }
}
