package io.mobsgeeks.oneway

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NoOpStateSerializerTest {
  private val noOpStateSerializer = NoOpStateSerializer<Cat>()
  private val cat = Cat()

  @Test fun `it returns the same object after serialization`() {
    // when
    val serializedCat = noOpStateSerializer.serialize(cat)

    // then
    assertThat(serializedCat)
        .isSameAs(cat)
  }

  @Test fun `it returns the same object after deserialization`() {
    // when
    val deserializedCat = noOpStateSerializer.deserialize(cat)

    // then
    assertThat(deserializedCat)
        .isSameAs(cat)
  }
}

class Cat
