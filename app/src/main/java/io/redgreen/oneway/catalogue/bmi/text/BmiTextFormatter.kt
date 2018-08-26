package io.redgreen.oneway.catalogue.bmi.text

import android.content.Context
import android.support.annotation.StringRes
import io.redgreen.oneway.catalogue.R
import io.redgreen.oneway.catalogue.bmi.calculator.BmiCategory
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem

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
      BmiCategory.VERY_SEVERELY_UNDERWEIGHT -> R.string.very_severely_underweight
      BmiCategory.SEVERELY_UNDERWEIGHT -> R.string.severely_underweight
      BmiCategory.UNDERWEIGHT -> R.string.underweight
      BmiCategory.NORMAL -> R.string.normal
      BmiCategory.OVERWEIGHT -> R.string.overweight
      BmiCategory.OBESE_CLASS_1 -> R.string.obese_class_1
      BmiCategory.OBESE_CLASS_2 -> R.string.obese_class_2
      BmiCategory.OBESE_CLASS_3 -> R.string.obese_class_3
      BmiCategory.OBESE_CLASS_4 -> R.string.obese_class_4
      BmiCategory.OBESE_CLASS_5 -> R.string.obese_class_5
      BmiCategory.OBESE_CLASS_6 -> R.string.obese_class_6
    }
    return context.getString(categoryTextRes)
  }

  fun getMeasurementSystemText(
      context: Context,
      measurementSystem: MeasurementSystem
  ): String {
    @StringRes val measurementSystemRes = when (measurementSystem) {
      MeasurementSystem.SI -> R.string.si_units
      MeasurementSystem.IMPERIAL -> R.string.imperial_units
    }
    return context.getString(measurementSystemRes)
  }
}
