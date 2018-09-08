/*
 * Copyright (C) 2018 Ragunath Jawahar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.redgreen.oneway.compiler

import com.google.common.truth.Truth.assertThat
import com.google.testing.compile.Compilation.Status.FAILURE
import com.google.testing.compile.Compilation.Status.SUCCESS
import com.google.testing.compile.Compiler.javac
import com.google.testing.compile.JavaFileObjects
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.*
import javax.tools.Diagnostic

internal class OneWayProcessorTest {
  private val processor = OneWayProcessor()
  private val compiler = javac().withProcessors(processor)

  @Test fun `it should throw error when annotation is applied to a non-interfaces`() {
    // given
    val annotatedClass = JavaFileObjects.forResource("MyViewGroupClass.java")

    // when
    val compilation = compiler
        .compile(annotatedClass)

    // then
    assertThat(compilation.status())
        .isEqualTo(FAILURE)

    val errors = compilation.errors()
    assertThat(errors.size)
        .isEqualTo(1)

    val error = errors.first()
    assertThat(error.kind)
        .isEqualTo(Diagnostic.Kind.ERROR)

    assertThat(error.getMessage(Locale.ENGLISH))
        .isEqualTo("'io.redgreen.oneway.compiler.test.MyViewGroupClass' is not an interface. @ViewGroups can only be applied to interfaces.")
  }

  @Disabled
  @Test fun `it should allow annotation on interfaces`() {
    // given
    val annotatedInterface = JavaFileObjects.forResource("MyViewGroupInterface.java")

    // when
    val compilation = compiler
        .compile(annotatedInterface)

    // then
    assertThat(compilation.status())
        .isEqualTo(SUCCESS)
  }

  @Disabled
  @Test fun `it should generate classes for the layouts specified`() {
    // given
    val annotatedInterface = JavaFileObjects.forResource("MyViewGroupInterface.java")

    // when
    val compilation = compiler
        .compile(annotatedInterface)

    // then
    val generatedSourceFiles = compilation.generatedSourceFiles()

    assertThat(generatedSourceFiles)
        .hasSize(1)

    val generatedJavaFileObject = generatedSourceFiles.first()
    assertThat(generatedJavaFileObject.name)
        .isEqualTo("/SOURCE_OUTPUT/io/redgreen/oneway/compiler/test/OneWayLinearLayout.java")
  }

  // TODO `it should ignore duplicates and just print a warning if a duplicate is found.`()
}
