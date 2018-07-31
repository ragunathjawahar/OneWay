package io.mobsgeeks.oneway.catalogue.bmi

import com.google.common.collect.Range
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BmiTest {
  @Test fun `when weight is 0, then BMI is 0`() {
    // when
    val bmiState = BmiState(0.0, 170.0)

    // then
    assertThat(bmiState.bmi)
        .isEqualTo(0.0)
  }

  @Test fun `when height is 0, then BMI is 0`() {
    // when
    val bmiState = BmiState(50.0, 0.0)

    // then
    assertThat(bmiState.bmi)
        .isEqualTo(0.0)
  }

  @Test fun `when height and weight are 0, then BMI is 0`() {
    // when
    val bmiState = BmiState(0.0, 0.0)

    // then
    assertThat(bmiState.bmi)
        .isEqualTo(0.0)
  }

  @Test(expected = IllegalStateException::class)
  fun `when weight is negative, then throw exception`() {
    BmiState(-1.0, 170.0)
  }

  @Test(expected = IllegalStateException::class)
  fun `when height is negative, then throw exception`() {
    BmiState(50.0, -150.0)
  }

  @Test fun `when weight is 50kg and height is 180cm, then BMI is 15dot4 (Underweight)`() {
    // when
    val bmiState = BmiState(50.0, 180.0)

    // then
    assertThat(bmiState.bmi)
        .isIn(Range.closed(15.4, 15.5))
  }

  @Test fun `when weight is 80kg and height is 180cm, then BMI is 24dot7 (Normal)`() {
    // when
    val bmiState = BmiState(80.0, 180.0)

    // then
    assertThat(bmiState.bmi)
        .isIn(Range.closed(24.6, 24.7))
  }

  @Test fun `when weight is 90kg and height is 180cm, then BMI is 27dot8 (Overweight)`() {
    // when
    val bmiState = BmiState(90.0, 180.0)

    // then
    assertThat(bmiState.bmi)
        .isIn(Range.closed(27.7, 27.8))
  }

  @Test fun `when weight is 100kg and height is 180cm, then BMI is 30dot9 (Obese)`() {
    // when
    val bmiState = BmiState(100.0, 180.0)

    // then
    assertThat(bmiState.bmi)
        .isIn(Range.closed(30.8, 30.9))
  }
}
