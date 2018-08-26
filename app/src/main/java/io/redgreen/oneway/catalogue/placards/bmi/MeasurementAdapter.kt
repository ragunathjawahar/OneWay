package io.redgreen.oneway.catalogue.placards.bmi

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

@LayoutRes private const val ITEM_VIEW = android.R.layout.simple_spinner_item
@LayoutRes private const val ITEM_VIEW_DROPDOWN = android.R.layout.simple_dropdown_item_1line
private const val ITEM_COUNT = 101

class MeasurementAdapter(
    context: Context,
    private val startValue: Int,
    private val unitSuffix: String
) : ArrayAdapter<Int>(context, ITEM_VIEW) {
  private val layoutInflater = LayoutInflater.from(context)

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
    val itemView = (convertView ?: layoutInflater.inflate(ITEM_VIEW, parent, false))
        as TextView

    val selectedValue = (startValue + position)
    itemView.text = "$selectedValue$unitSuffix"

    return itemView
  }

  override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
    val itemView = (convertView
        ?: layoutInflater.inflate(ITEM_VIEW_DROPDOWN, parent, false)) as TextView

    val selectedValue = (startValue + position)
    itemView.text = "$selectedValue$unitSuffix"

    return itemView
  }

  override fun getCount(): Int =
      ITEM_COUNT
}
