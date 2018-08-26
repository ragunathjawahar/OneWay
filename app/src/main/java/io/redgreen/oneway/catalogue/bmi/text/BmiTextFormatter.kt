package io.redgreen.oneway.catalogue.bmi.text

import android.content.Context
import android.support.annotation.StringRes
import io.redgreen.oneway.catalogue.R
import io.redgreen.oneway.catalogue.bmi.calculator.BmiCategory
import io.redgreen.oneway.catalogue.bmi.calculator.BmiCategory.*
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem.IMPERIAL
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem.SI

object BmiTextFormatter {
  fun getBmiText(
      context: Context,
      bmi: Double
  ): String =
      context.getString(R.string.template_bmi, bmi)

  fun getBmiCategoryText(
      context: Context,
      category: BmiCategory
  ): String {
    val categoryTextRes = when(category) {
      VERY_SEVERELY_UNDERWEIGHT -> R.string.very_severely_underweight
      SEVERELY_UNDERWEIGHT -> R.string.severely_underweight
      UNDERWEIGHT -> R.string.underweight
      NORMAL -> R.string.normal
      OVERWEIGHT -> R.string.overweight
      OBESE_CLASS_1 -> R.string.obese_class_1
      OBESE_CLASS_2 -> R.string.obese_class_2
      OBESE_CLASS_3 -> R.string.obese_class_3
      OBESE_CLASS_4 -> R.string.obese_class_4
      OBESE_CLASS_5 -> R.string.obese_class_5
      OBESE_CLASS_6 -> R.string.obese_class_6
    }
    return context.getString(categoryTextRes)
  }

  fun getMeasurementSystemText(
      context: Context,
      measurementSystem: MeasurementSystem
  ): String {
    @StringRes val measurementSystemRes = when (measurementSystem) {
      SI -> R.string.si_units
      IMPERIAL -> R.string.imperial_units
    }
    return context.getString(measurementSystemRes)
  }

  fun getWeightText(
      context: Context,
      weight: Double,
      measurementSystem: MeasurementSystem
  ): String {
    @StringRes val weightTemplateStringRes = when (measurementSystem) {
      SI -> R.string.template_weight_si
      IMPERIAL -> R.string.template_weight_imperial
    }
    return context.getString(weightTemplateStringRes, weight)
  }

  fun getHeightText(
      context: Context,
      height: Double,
      measurementSystem: MeasurementSystem
  ): String {
    @StringRes val heightTemplateStringRes = when (measurementSystem) {
      SI -> R.string.template_height_si
      IMPERIAL -> R.string.template_height_imperial
    }
    return context.getString(heightTemplateStringRes, height)
  }
}
