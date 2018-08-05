package io.mobsgeeks.oneway.android

import android.os.Bundle
import android.support.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import io.mobsgeeks.oneway.android.fixtures.Student
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ParcelablePersisterTest {
  private val persister = ParcelablePersister<Student>()
  private val student = Student("For Life")
  private val bundle = Bundle(1)

  @Test fun writeToBundle() {
    // when
    persister.write(student, bundle)

    // then
    val readStudent = persister.read(bundle)
    assertThat(readStudent)
        .isEqualTo(student)
  }

  @Test fun readFromBundle() {
    // given
    persister.write(student, bundle)

    // when
    val readStudent = persister.read(bundle)

    // then
    assertThat(readStudent)
        .isNotNull()
    assertThat(readStudent)
        .isEqualTo(student)
  }

  @Test fun readReturnsNullIfBundleIsEmpty() {
    // when
    val nonExistentStudent = persister.read(bundle)

    // then
    assertThat(nonExistentStudent)
        .isNull()
  }
}
