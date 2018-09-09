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

import com.squareup.javapoet.*
import javax.lang.model.element.Modifier.*

class OneWayViewGroupClass(
    parentClassFqcn: String,
    classToGenerateFqcn: String
) {
  companion object {
    private const val FQCN_ANDROID_MVI_CONTRACT = "io.redgreen.oneway.android.barebones.AndroidMviContract"
    private const val FQCN_ANDROID_MVI_DELEGATE = "io.redgreen.oneway.android.barebones.AndroidMviDelegate"
    private const val FQCN_ATTRIBUTE_SET = "android.util.AttributeSet"
    private const val FQCN_CALL_SUPER = "android.support.annotation.CallSuper"
    private const val FQCN_CONTEXT = "android.content.Context"
    private const val FQCN_NO_OP_STATE_CONVERTER = "io.redgreen.oneway.NoOpStateConverter"
    private const val FQCN_NON_NULL = "android.support.annotation.NonNull"
    private const val FQCN_NULLABLE = "android.support.annotation.Nullable"
    private const val FQCN_OBSERVABLE = "io.reactivex.Observable"
    private const val FQCN_OVERRIDE = "java.lang.Override"
    private const val FQCN_BUNDLE = "android.os.Bundle"
    private const val FQCN_PARCELABLE = "android.os.Parcelable"
    private const val FQCN_PARCELABLE_PERSISTER = "io.redgreen.oneway.android.ParcelablePersister"
    private const val FQCN_PERSISTER = "io.redgreen.oneway.android.barebones.Persister"
    private const val FQCN_STATE_CONVERTER = "io.redgreen.oneway.StateConverter"

    private const val TYPE_NAME_STATE = "S"

    private const val FIELD_NAME_KEY_SUPER_CLASS_STATE = "KEY_SUPER_CLASS_STATE"
    private const val FIELD_NAME_ANDROID_MVI_DELEGATE = "androidMviDelegate"
    private const val PARAM_NAME_ATTRIBUTE_SET = "attrs"
    private const val PARAM_NAME_CONTEXT = "context"
  }

  private val parentClassName = ClassName.bestGuess(parentClassFqcn)
  private val classToGenerateClassName = ClassName.bestGuess(classToGenerateFqcn)

  private val attributeSetClassName = ClassName.bestGuess(FQCN_ATTRIBUTE_SET)
  private val bundleClassName = ClassName.bestGuess(FQCN_BUNDLE)
  private val contextClassName = ClassName.bestGuess(FQCN_CONTEXT)
  private val parcelableClassName = ClassName.bestGuess(FQCN_PARCELABLE)

  private val overrideAnnotationSpec = AnnotationSpec
      .builder(ClassName.bestGuess(FQCN_OVERRIDE))
      .build()

  private val callSuperAnnotationSpec = AnnotationSpec
      .builder(ClassName.bestGuess(FQCN_CALL_SUPER))
      .build()

  private val nonNullAnnotationSpec = AnnotationSpec
      .builder(ClassName.bestGuess(FQCN_NON_NULL))
      .build()

  fun brewJava(): JavaFile {
    val androidMviContractInterfaceClassName = ClassName.bestGuess(FQCN_ANDROID_MVI_CONTRACT)
    val stateTypeVariableName = TypeVariableName.get(TYPE_NAME_STATE)

    val viewGroupTypeSpec = getViewGroupTypeSpecBuilder(stateTypeVariableName, androidMviContractInterfaceClassName)
        .addField(getSuperClassStateConstantFieldSpec())
        .addField(getAndroidMviDelegateFieldSpec(stateTypeVariableName))
        .addMethods(getConstructors())
        .addMethod(getStateConverterMethodSpec(stateTypeVariableName))
        .addMethod(getPersisterMethodSpec(stateTypeVariableName))
        .addMethod(getTimelineMethodSpec(stateTypeVariableName))
        .addMethod(getOnAttachedToWindowMethodSpec())
        .addMethod(getOnDetachedFromWindowMethodSpec())
        .addMethod(getOnSaveInstanceStateMethodSpec())
        .addMethod(getOnRestoreInstanceStateMethodSpec())
        .build()

    return JavaFile
        .builder(classToGenerateClassName.packageName(), viewGroupTypeSpec)
        .skipJavaLangImports(true)
        .build()
  }

  private fun getViewGroupTypeSpecBuilder(
      stateTypeVariable: TypeVariableName,
      androidMviContractInterfaceClassName: ClassName
  ): TypeSpec.Builder {
    val androidMviContractTypeName = ParameterizedTypeName
        .get(androidMviContractInterfaceClassName, stateTypeVariable, stateTypeVariable)

    return TypeSpec
        .classBuilder(classToGenerateClassName)
        .addModifiers(PUBLIC, ABSTRACT)
        .addTypeVariable(stateTypeVariable.withBounds(parcelableClassName))
        .superclass(parentClassName)
        .addSuperinterface(androidMviContractTypeName)
  }

  private fun getSuperClassStateConstantFieldSpec(): FieldSpec {
    return FieldSpec
        .builder(String::class.java, FIELD_NAME_KEY_SUPER_CLASS_STATE, PRIVATE, STATIC, FINAL)
        .initializer("\u0024S", "super_class_state")
        .build()
  }

  private fun getAndroidMviDelegateFieldSpec(
      stateTypeVariableName: TypeVariableName
  ): FieldSpec {
    val androidMviDelegateClassName = ClassName.bestGuess(FQCN_ANDROID_MVI_DELEGATE)
    val parameterizedAndroidMviDelegate = ParameterizedTypeName
        .get(androidMviDelegateClassName, stateTypeVariableName, stateTypeVariableName)

    return FieldSpec
        .builder(parameterizedAndroidMviDelegate, FIELD_NAME_ANDROID_MVI_DELEGATE, PRIVATE, FINAL)
        .initializer("new \u0024T<>(this)", androidMviDelegateClassName)
        .build()
  }

  private fun getConstructors(): List<MethodSpec> {
    return listOf(
        getSingleArgConstructor(),
        getDoubleArgConstructor(),
        getTripleArgConstructor()
    )
  }

  private fun getSingleArgConstructor(): MethodSpec {
    return MethodSpec
        .constructorBuilder()
        .addModifiers(PUBLIC)
        .addParameter(contextClassName, PARAM_NAME_CONTEXT)
        .addStatement("super(\u0024L)", PARAM_NAME_CONTEXT)
        .build()
  }

  private fun getDoubleArgConstructor(): MethodSpec {
    return MethodSpec
        .constructorBuilder()
        .addModifiers(PUBLIC)
        .addParameter(contextClassName, PARAM_NAME_CONTEXT)
        .addParameter(attributeSetClassName, PARAM_NAME_ATTRIBUTE_SET)
        .addStatement("super(\u0024L, \u0024L)", PARAM_NAME_CONTEXT, PARAM_NAME_ATTRIBUTE_SET)
        .build()
  }

  private fun getTripleArgConstructor(): MethodSpec {
    val paramNameDefStyleAttr = "defStyleAttr"

    return MethodSpec
        .constructorBuilder()
        .addModifiers(PUBLIC)
        .addParameter(contextClassName, PARAM_NAME_CONTEXT)
        .addParameter(attributeSetClassName, PARAM_NAME_ATTRIBUTE_SET)
        .addParameter(Int::class.java, paramNameDefStyleAttr)
        .addStatement("super(\u0024L, \u0024L, \u0024L)", PARAM_NAME_CONTEXT, PARAM_NAME_ATTRIBUTE_SET, paramNameDefStyleAttr)
        .build()
  }

  private fun getStateConverterMethodSpec(
      stateTypeVariableName: TypeVariableName
  ): MethodSpec {
    val stateConverterTypeName = ParameterizedTypeName
        .get(ClassName.bestGuess(FQCN_STATE_CONVERTER), stateTypeVariableName, stateTypeVariableName)

    return MethodSpec
        .methodBuilder("getStateConverter")
        .returns(stateConverterTypeName)
        .addAnnotation(nonNullAnnotationSpec)
        .addAnnotation(overrideAnnotationSpec)
        .addModifiers(PUBLIC)
        .addStatement("return new \u0024T<>()", ClassName.bestGuess(FQCN_NO_OP_STATE_CONVERTER))
        .build()
  }

  private fun getPersisterMethodSpec(
      stateTypeVariableName: TypeVariableName
  ): MethodSpec {
    val persisterTypeName = ParameterizedTypeName
        .get(ClassName.bestGuess(FQCN_PERSISTER), stateTypeVariableName)

    return MethodSpec
        .methodBuilder("getPersister")
        .returns(persisterTypeName)
        .addAnnotation(nonNullAnnotationSpec)
        .addAnnotation(overrideAnnotationSpec)
        .addModifiers(PUBLIC)
        .addStatement(
            "return new \u0024T<>(\u0024L)",
            ClassName.bestGuess(FQCN_PARCELABLE_PERSISTER),
            FIELD_NAME_KEY_SUPER_CLASS_STATE
        )
        .build()
  }

  private fun getTimelineMethodSpec(
      stateTypeVariableName: TypeVariableName
  ): MethodSpec {
    val observableTypeName = ParameterizedTypeName
        .get(ClassName.bestGuess(FQCN_OBSERVABLE), stateTypeVariableName)
    val methodName = "getTimeline"

    return MethodSpec
        .methodBuilder(methodName)
        .returns(observableTypeName)
        .addAnnotation(nonNullAnnotationSpec)
        .addAnnotation(overrideAnnotationSpec)
        .addModifiers(PUBLIC, FINAL)
        .addStatement("return \u0024L.\u0024L()", FIELD_NAME_ANDROID_MVI_DELEGATE, methodName)
        .build()
  }

  private fun getOnAttachedToWindowMethodSpec(): MethodSpec {
    val methodName = "onAttachedToWindow"

    return MethodSpec
        .methodBuilder(methodName)
        .addModifiers(PROTECTED)
        .addAnnotation(callSuperAnnotationSpec)
        .addAnnotation(overrideAnnotationSpec)
        .addStatement("super.\u0024L()", methodName)
        .addStatement(
            "\u0024L.bind()",
            FIELD_NAME_ANDROID_MVI_DELEGATE
        )
        .build()
  }

  private fun getOnDetachedFromWindowMethodSpec(): MethodSpec {
    val methodName = "onDetachedFromWindow"
    return MethodSpec
        .methodBuilder(methodName)
        .addModifiers(PROTECTED)
        .addAnnotation(callSuperAnnotationSpec)
        .addAnnotation(overrideAnnotationSpec)
        .addStatement("\u0024L.unbind()", FIELD_NAME_ANDROID_MVI_DELEGATE)
        .addStatement("super.\u0024L()", methodName)
        .build()
  }

  private fun getOnSaveInstanceStateMethodSpec(): MethodSpec {
    val methodName = "onSaveInstanceState"
    val viewStateVariableName = "viewState"
    val outStateVariableName = "outState"
    val nullableAnnotationSpec = AnnotationSpec
        .builder(ClassName.bestGuess(FQCN_NULLABLE))
        .build()

    return MethodSpec
        .methodBuilder(methodName)
        .addModifiers(PROTECTED)
        .returns(parcelableClassName)
        .addAnnotation(nullableAnnotationSpec)
        .addAnnotation(callSuperAnnotationSpec)
        .addAnnotation(overrideAnnotationSpec)
        .addStatement("\u0024T \u0024L = super.\u0024L()", parcelableClassName, viewStateVariableName, methodName)
        .addStatement("\u0024T \u0024L = new \u0024T()", bundleClassName, outStateVariableName, bundleClassName)
        .addStatement("\u0024L.putParcelable(\u0024L, \u0024L)", outStateVariableName, FIELD_NAME_KEY_SUPER_CLASS_STATE, viewStateVariableName)
        .addStatement("\u0024L.saveState(\u0024L)", FIELD_NAME_ANDROID_MVI_DELEGATE, outStateVariableName)
        .addStatement("return \u0024L", outStateVariableName)
        .build()
  }

  private fun getOnRestoreInstanceStateMethodSpec(): MethodSpec {
    val methodName = "onRestoreInstanceState"
    val stateParameterName = "state"

    return MethodSpec
        .methodBuilder(methodName)
        .addModifiers(PROTECTED)
        .addParameter(parcelableClassName, stateParameterName)
        .addAnnotation(callSuperAnnotationSpec)
        .addAnnotation(overrideAnnotationSpec)
        .addStatement(
            "\u0024L.restoreState((\u0024T) \u0024L)",
            FIELD_NAME_ANDROID_MVI_DELEGATE,
            bundleClassName,
            stateParameterName
        )
        .addStatement(
            "super.\u0024L(((\u0024T) \u0024L).getParcelable(\u0024L))",
            methodName,
            bundleClassName,
            stateParameterName,
            FIELD_NAME_KEY_SUPER_CLASS_STATE
        )
        .build()
  }
}
