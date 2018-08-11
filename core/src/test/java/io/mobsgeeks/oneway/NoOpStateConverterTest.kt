package io.mobsgeeks.oneway

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NoOpStateConverterTest {
  private val noOpStateConverter = NoOpStateConverter<Cat>()
  private val cat = Cat()

  @Test fun `it returns the same object after serialization`() {
    // when
    val toCat = noOpStateConverter.to(cat)

    // then
    assertThat(toCat)
        .isSameAs(cat)
  }

  @Test fun `it returns the same object after deserialization`() {
    // when
    val fromCat = noOpStateConverter.from(cat)

    // then
    assertThat(fromCat)
        .isSameAs(cat)
  }
}

class Cat
