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

import io.redgreen.oneway.annotations.ViewGroups
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.ElementKind
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

class OneWayProcessor : AbstractProcessor() {
  companion object {
    private const val GENERATED_LAYOUT_CLASS_PREFIX = "OneWay"
  }

  private lateinit var elementUtils: Elements
  private lateinit var messager: Messager
  private lateinit var filer: Filer

  @Synchronized
  override fun init(
      processingEnvironment: ProcessingEnvironment
  ) {
    super.init(processingEnvironment)
    elementUtils = processingEnvironment.elementUtils
    messager = processingEnvironment.messager
    filer = processingEnvironment.filer
  }

  override fun process(
      set: Set<TypeElement>,
      roundEnvironment: RoundEnvironment
  ): Boolean {
    if (roundEnvironment.processingOver()) {
      return false
    }

    val viewGroupsAnnotatedElements = roundEnvironment
        .getElementsAnnotatedWith(ViewGroups::class.java)

    if (viewGroupsAnnotatedElements.isEmpty()) {
      return false
    }

    for (annotatedElement in viewGroupsAnnotatedElements) {
      if (annotatedElement.kind != ElementKind.INTERFACE) {
        printErrorAnnotatedElementNotAnInterface(annotatedElement.asType())
      } else {
        val packageElement = annotatedElement.enclosingElement as PackageElement

        // TODO(rj) 4/Sep/18 - What if package is empty or null?
        // TODO(rj) 4/Sep/18 - What if interface is present inside an inner class?

        val viewGroupsTypeElement = elementUtils.getTypeElement(ViewGroups::class.java.name)
        for (annotationMirror in annotatedElement.annotationMirrors) {
          if (viewGroupsTypeElement == annotationMirror.annotationType.asElement()) {
            val entries = annotationMirror
                .elementValues
                .entries
            for ((key, value) in entries) {
              if ("value" == key.simpleName.toString()) {
                val viewGroupClasses = value.value as AbstractCollection<out AnnotationValue>
                for (viewGroupClass in viewGroupClasses) {
                  val viewGroupTypeElement = elementUtils.getTypeElement(viewGroupClass.value.toString())

                  val packageName = packageElement.qualifiedName.toString()
                  val className = GENERATED_LAYOUT_CLASS_PREFIX + viewGroupTypeElement.simpleName.toString()

                  val parentClassFqcn = viewGroupTypeElement.qualifiedName.toString()
                  val classToGenerateFqcn = "$packageName.$className"

                  OneWayViewGroupClass(parentClassFqcn, classToGenerateFqcn)
                      .brewJava()
                      .writeTo(filer)
                  return true
                }
              }
            }
          }
        }
      }
    }

    return false // TODO(rj) 5/Sep/18 - Figure out when to return false, when to return true?
  }

  override fun getSupportedSourceVersion(): SourceVersion {
    return SourceVersion.latestSupported()
  }

  override fun getSupportedAnnotationTypes(): Set<String> {
    val annotationName = ViewGroups::class.java.canonicalName
    return HashSet(listOf(annotationName))
  }

  private fun printErrorAnnotatedElementNotAnInterface(typeMirror: TypeMirror) {
    val message = String
        .format(
            "'%s' is not an interface. @%s can only be applied to interfaces.",
            typeMirror, ViewGroups::class.java.simpleName
        )
    error(message)
  }

  private fun error(message: String) {
    messager.printMessage(Diagnostic.Kind.ERROR, message)
  }
}
