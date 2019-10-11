/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gwt.jsonix.marshallers.xjc.plugin.builders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.xml.namespace.QName;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import gwt.jsonix.marshallers.xjc.plugin.AbstractBuilderTest;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsProperty;
import jsinterop.base.JsArrayLike;
import org.junit.Test;

import static gwt.jsonix.marshallers.xjc.plugin.TestUtils.getJDefinedClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ModelBuilderTest extends AbstractBuilderTest {

    @Test
    public void addGetTypeNameProperty() {
        // TODO {gcardosi}: do we really have to use Mockito here?
    }

    @Test
    public void addProperty() {
        // TODO {gcardosi}: do we really have to use Mockito here?
    }

    @Test
    public void addOtherAttributesProperty() throws JClassAlreadyExistsException {
        final JDefinedClass testClass = getJDefinedClass(jCodeModel, "net.gwt.jsonix", "TestClass", null);
        ModelBuilder.addOtherAttributesProperty(jCodeModel, testClass, referredJDefinedClass, "unused");
        assertEquals(3, testClass.methods().size());
        final Optional<JMethod> nativeGetterOptional = testClass.methods().stream().filter(method -> Objects.equals("getOtherAttributes", method.name())).findFirst();
        final Optional<JMethod> nativeSetterOptional = testClass.methods().stream().filter(method -> Objects.equals("setOtherAttributes", method.name())).findFirst();
        final Optional<JMethod> staticGetterOptional = testClass.methods().stream().filter(method -> Objects.equals("getOtherAttributesMap", method.name())).findFirst();
        assertTrue(nativeGetterOptional.isPresent());
        assertTrue(nativeSetterOptional.isPresent());
        assertTrue(staticGetterOptional.isPresent());
        final JClass propertyRef = jCodeModel.ref(Map.class).narrow(QName.class, String.class);
        commonAddGetterMethod(nativeGetterOptional.get(), propertyRef, "OtherAttributes", JMod.PUBLIC + JMod.NATIVE, JsProperty.class);
        commonAddSetterMethod(nativeSetterOptional.get(), propertyRef, "OtherAttributes", "otherAttributes", JMod.PUBLIC + JMod.FINAL + JMod.NATIVE, JsProperty.class);
        commonStaticGetOtherAttributesMethod(staticGetterOptional.get(), propertyRef, testClass);
    }

    @Test
    public void addStaticOtherAttributesGetter() throws JClassAlreadyExistsException {
        final JDefinedClass testClass = getJDefinedClass(jCodeModel, "net.gwt.jsonix", "TestClass", null);
        final JClass parameterRef = jCodeModel.ref(Map.class).narrow(QName.class, String.class);
        final JMethod getMethod = testClass.method(JMod.PUBLIC + JMod.NATIVE, parameterRef, "getMethod");
        ModelBuilder.addStaticOtherAttributesGetter(jCodeModel, testClass, getMethod, referredJDefinedClass);
        assertEquals(2, testClass.methods().size());
        final Optional<JMethod> retrievedOptional = testClass.methods().stream().filter(method -> Objects.equals("getOtherAttributesMap", method.name())).findFirst();
        assertTrue(retrievedOptional.isPresent());
        commonStaticGetOtherAttributesMethod(retrievedOptional.get(), parameterRef, testClass);
    }

    @Test
    public void getPropertyRef() {
        // TODO {gcardosi}: do we really have to use Mockito here?
    }

    @Test
    public void getOrCreatePropertyRefFromCPropertyInfo() {
        // TODO {gcardosi}: do we really have to use Mockito here?
    }

    @Test
    public void getOrCreatePropertyRefFromOriginalClassName() {
        // TODO {gcardosi}: do we really have to use Mockito here?
    }

    @Test
    public void getOriginalClassName() {
        // TODO {gcardosi}: do we really have to use Mockito here?
    }

    @Test
    public void getPropertyClassName() {
        // TODO {gcardosi}: do we really have to use Mockito here?
    }

    @Test
    public void addGetterJsArrayLikePrimitive() throws JClassAlreadyExistsException {
        JClass primitiveRef = jCodeModel.ref(Integer.class);
        JClass jsArrayLikeRef = jCodeModel.ref(JsArrayLike.class).narrow(primitiveRef);
        String publicPropertyName = "ParameterRef";
        String privatePropertyName = "parameterRef";
        final JDefinedClass retrieved = commonAddGetter(jsArrayLikeRef, 5, publicPropertyName, privatePropertyName);
        Optional<JMethod> optionalListGetter = retrieved.methods().stream().filter(jMethod -> Objects.equals("get" + publicPropertyName, jMethod.name())).findFirst();
        Optional<JMethod> optionalAdd = retrieved.methods().stream().filter(jMethod -> Objects.equals("add" + publicPropertyName, jMethod.name())).findFirst();
        Optional<JMethod> optionalAddAll = retrieved.methods().stream().filter(jMethod -> Objects.equals("addAll" + publicPropertyName, jMethod.name())).findFirst();
        Optional<JMethod> optionalRemove = retrieved.methods().stream().filter(jMethod -> Objects.equals("remove" + publicPropertyName, jMethod.name())).findFirst();
        Optional<JMethod> optionalNativeGetter = retrieved.methods().stream().filter(jMethod -> Objects.equals("getNative" + publicPropertyName, jMethod.name())).findFirst();
        assertTrue(optionalListGetter.isPresent());
        assertTrue(optionalAdd.isPresent());
        assertTrue(optionalAddAll.isPresent());
        assertTrue(optionalRemove.isPresent());
        assertTrue(optionalNativeGetter.isPresent());
        commonAddGetterMethod(optionalListGetter.get(), jCodeModel.ref(List.class).narrow(primitiveRef), publicPropertyName, JMod.PUBLIC + JMod.FINAL, JsOverlay.class);
        commonParamMethod("add", optionalAdd.get(), primitiveRef.unboxify(), publicPropertyName, "element", JMod.PUBLIC + JMod.FINAL, JsOverlay.class);
        commonVarargMethod("addAll", optionalAddAll.get(), primitiveRef.unboxify().array(), publicPropertyName, "elements", JMod.PUBLIC + JMod.FINAL, JsOverlay.class);
        commonParamMethod("remove", optionalRemove.get(), jCodeModel._ref(int.class), publicPropertyName, "index", JMod.PUBLIC + JMod.FINAL, JsOverlay.class);
        commonAddGetterMethod(optionalNativeGetter.get(), jsArrayLikeRef, "Native" + publicPropertyName, JMod.PUBLIC + JMod.NATIVE, JsProperty.class);
    }

    @Test
    public void addGetterJsArrayLikeNotPrimitive() throws JClassAlreadyExistsException {
        JClass jsArrayLikeRef = jCodeModel.ref(JsArrayLike.class).narrow(parameterRef);
        String publicPropertyName = "ParameterRef";
        String privatePropertyName = "parameterRef";
        final JDefinedClass retrieved = commonAddGetter(jsArrayLikeRef, 5, publicPropertyName, privatePropertyName);
        Optional<JMethod> optionalListGetter = retrieved.methods().stream().filter(jMethod -> Objects.equals("get" + publicPropertyName, jMethod.name())).findFirst();
        Optional<JMethod> optionalAdd = retrieved.methods().stream().filter(jMethod -> Objects.equals("add" + publicPropertyName, jMethod.name())).findFirst();
        Optional<JMethod> optionalAddAll = retrieved.methods().stream().filter(jMethod -> Objects.equals("addAll" + publicPropertyName, jMethod.name())).findFirst();
        Optional<JMethod> optionalRemove = retrieved.methods().stream().filter(jMethod -> Objects.equals("remove" + publicPropertyName, jMethod.name())).findFirst();
        Optional<JMethod> optionalNativeGetter = retrieved.methods().stream().filter(jMethod -> Objects.equals("getNative" + publicPropertyName, jMethod.name())).findFirst();
        assertTrue(optionalListGetter.isPresent());
        assertTrue(optionalAdd.isPresent());
        assertTrue(optionalAddAll.isPresent());
        assertTrue(optionalRemove.isPresent());
        assertTrue(optionalNativeGetter.isPresent());
        commonAddGetterMethod(optionalListGetter.get(), jCodeModel.ref(List.class).narrow(parameterRef), publicPropertyName, JMod.PUBLIC + JMod.FINAL, JsOverlay.class);
        commonParamMethod("add", optionalAdd.get(), optionalAdd.get().generify("D", parameterRef), publicPropertyName, "element", JMod.PUBLIC + JMod.FINAL, JsOverlay.class);
        commonVarargMethod("addAll", optionalAddAll.get(), optionalAdd.get().generify("D", parameterRef).array(), publicPropertyName, "elements", JMod.PUBLIC + JMod.FINAL, JsOverlay.class);
        commonParamMethod("remove", optionalRemove.get(), jCodeModel._ref(int.class), publicPropertyName, "index", JMod.PUBLIC + JMod.FINAL, JsOverlay.class);
        commonAddGetterMethod(optionalNativeGetter.get(), jsArrayLikeRef, "Native" + publicPropertyName, JMod.PUBLIC + JMod.NATIVE, JsProperty.class);
    }

    @Test
    public void addGetterArray() throws JClassAlreadyExistsException {
        JClass arrayRef = parameterRef.array();
        String publicPropertyName = "ParameterRef";
        String privatePropertyName = "parameterRef";
        final JDefinedClass retrieved = commonAddGetter(arrayRef, 5, publicPropertyName, privatePropertyName);
        Optional<JMethod> optionalListGetter = retrieved.methods().stream().filter(jMethod -> Objects.equals("get" + publicPropertyName, jMethod.name())).findFirst();
        Optional<JMethod> optionalAdd = retrieved.methods().stream().filter(jMethod -> Objects.equals("add" + publicPropertyName, jMethod.name())).findFirst();
        Optional<JMethod> optionalAddAll = retrieved.methods().stream().filter(jMethod -> Objects.equals("addAll" + publicPropertyName, jMethod.name())).findFirst();
        Optional<JMethod> optionalRemove = retrieved.methods().stream().filter(jMethod -> Objects.equals("remove" + publicPropertyName, jMethod.name())).findFirst();
        Optional<JMethod> optionalNativeGetter = retrieved.methods().stream().filter(jMethod -> Objects.equals("getNative" + publicPropertyName, jMethod.name())).findFirst();
        assertTrue(optionalListGetter.isPresent());
        assertTrue(optionalAdd.isPresent());
        assertTrue(optionalAddAll.isPresent());
        assertTrue(optionalRemove.isPresent());
        assertTrue(optionalNativeGetter.isPresent());
        commonAddGetterMethod(optionalListGetter.get(), jCodeModel.ref(List.class).narrow(parameterRef), publicPropertyName, JMod.PUBLIC + JMod.FINAL, JsOverlay.class);
        commonParamMethod("add", optionalAdd.get(), parameterRef, publicPropertyName, "element", JMod.PUBLIC + JMod.FINAL, JsOverlay.class);
        commonVarargMethod("addAll", optionalAddAll.get(), arrayRef, publicPropertyName, "elements", JMod.PUBLIC + JMod.FINAL, JsOverlay.class);
        commonParamMethod("remove", optionalRemove.get(), jCodeModel._ref(int.class), publicPropertyName, "index", JMod.PUBLIC + JMod.FINAL, JsOverlay.class);
        commonAddGetterMethod(optionalNativeGetter.get(), arrayRef, "Native" + publicPropertyName, JMod.PUBLIC + JMod.NATIVE, JsProperty.class);
    }

    @Test
    public void addGetterSimple() throws JClassAlreadyExistsException {
        String publicPropertyName = "ParameterRef";
        String privatePropertyName = "parameterRef";
        final JDefinedClass retrieved = commonAddGetter(parameterRef, 1, publicPropertyName, privatePropertyName);
        commonAddGetterMethod(retrieved.methods().iterator().next(), parameterRef, publicPropertyName, JMod.PUBLIC + JMod.NATIVE, JsProperty.class);
    }

    @Test
    public void addSetterJsArrayLike() throws JClassAlreadyExistsException {
        JClass jsArrayLikeRef = jCodeModel.ref(JsArrayLike.class).narrow(parameterRef);
        String publicPropertyName = "ParameterRef";
        String privatePropertyName = "parameterRef";
        final JDefinedClass retrieved = commonAddSetter(jsArrayLikeRef, 2, publicPropertyName, privatePropertyName);
        Optional<JMethod> optionalListSetter = retrieved.methods().stream().filter(jMethod -> Objects.equals("set" + publicPropertyName, jMethod.name())).findFirst();
        Optional<JMethod> optionalNativeSetter = retrieved.methods().stream().filter(jMethod -> Objects.equals("setNative" + publicPropertyName, jMethod.name())).findFirst();
        assertTrue(optionalListSetter.isPresent());
        assertTrue(optionalNativeSetter.isPresent());
        commonAddSetterMethod(optionalListSetter.get(), jCodeModel.ref(List.class).narrow(parameterRef), publicPropertyName, privatePropertyName, JMod.PUBLIC + JMod.FINAL, JsOverlay.class);
        commonAddSetterMethod(optionalNativeSetter.get(), jsArrayLikeRef, "Native" + publicPropertyName, privatePropertyName, JMod.PUBLIC + JMod.FINAL + JMod.NATIVE, JsProperty.class);
    }

    @Test
    public void addSetterArray() throws JClassAlreadyExistsException {
        JClass arrayRef = parameterRef.array();
        String publicPropertyName = "ParameterRef";
        String privatePropertyName = "parameterRef";
        final JDefinedClass retrieved = commonAddSetter(arrayRef, 2, publicPropertyName, privatePropertyName);
        Optional<JMethod> optionalListSetter = retrieved.methods().stream().filter(jMethod -> Objects.equals("set" + publicPropertyName, jMethod.name())).findFirst();
        Optional<JMethod> optionalNativeSetter = retrieved.methods().stream().filter(jMethod -> Objects.equals("setNative" + publicPropertyName, jMethod.name())).findFirst();
        assertTrue(optionalListSetter.isPresent());
        assertTrue(optionalNativeSetter.isPresent());
        commonAddSetterMethod(optionalListSetter.get(), jCodeModel.ref(List.class).narrow(parameterRef), publicPropertyName, privatePropertyName, JMod.PUBLIC + JMod.FINAL, JsOverlay.class);
        commonAddSetterMethod(optionalNativeSetter.get(), arrayRef, "Native" + publicPropertyName, privatePropertyName, JMod.PUBLIC + JMod.FINAL + JMod.NATIVE, JsProperty.class);
    }

    @Test
    public void addSetterSimple() throws JClassAlreadyExistsException {
        String publicPropertyName = "ParameterRef";
        String privatePropertyName = "parameterRef";
        final JDefinedClass retrieved = commonAddSetter(parameterRef, 1, publicPropertyName, privatePropertyName);
        commonAddSetterMethod(retrieved.methods().iterator().next(), parameterRef, publicPropertyName, privatePropertyName, JMod.PUBLIC + JMod.FINAL + JMod.NATIVE, JsProperty.class);
    }

    @Test
    public void parseClass() {
        String fullName = "foo.bar.Fake";
        final JClass retrieved = ModelBuilder.parseClass(fullName, jCodeModel, new HashMap<>());
        assertNotNull(retrieved);
        assertEquals(fullName, retrieved.fullName());
    }

    @Test
    public void getJNIRepresentation() throws JClassAlreadyExistsException {
        JDefinedClass testClass = getJDefinedClass(jCodeModel, "net.gwt.jsonix", "TestClass", null);
        String retrieved = ModelBuilder.getJNIRepresentation(testClass);
        assertEquals("Lnet/gwt/jsonix/TestClass", retrieved);
        testClass = getJDefinedClass(jCodeModel, "net.gwt.jsonix", "TestClass", "OuterClass");
        retrieved = ModelBuilder.getJNIRepresentation(testClass);
        assertEquals("Lnet/gwt/jsonix/OuterClass$TestClass", retrieved);
    }

    private JDefinedClass commonAddGetter(JClass propertyRef, int expectedMethods, String publicPropertyName, String privatePropertyName) throws JClassAlreadyExistsException {
        JDefinedClass testClass = getJDefinedClass(jCodeModel, "net.gwt.jsonix", "TestClass", null);
        ModelBuilder.addGetter(jCodeModel, testClass, referredJDefinedClass, propertyRef, publicPropertyName, privatePropertyName);
        assertNotNull(testClass);
        assertEquals(expectedMethods, testClass.methods().size());
        return testClass;
    }

    private JDefinedClass commonAddSetter(JClass propertyRef, int expectedMethods, String publicPropertyName, String privatePropertyName) throws JClassAlreadyExistsException {
        JDefinedClass testClass = getJDefinedClass(jCodeModel, "net.gwt.jsonix", "TestClass", null);
        ModelBuilder.addSetter(jCodeModel, testClass, propertyRef, publicPropertyName, privatePropertyName, referredJDefinedClass);
        assertNotNull(testClass);
        assertEquals(expectedMethods, testClass.methods().size());
        return testClass;
    }

    private void commonStaticGetOtherAttributesMethod(JMethod retrieved, JClass returnRef, JClass parameterRef) {
        assertEquals(JMod.PUBLIC + JMod.STATIC, retrieved.mods().getValue());
        assertEquals(returnRef.binaryName(), retrieved.type().binaryName());
        assertEquals(1, retrieved.params().size());
        final JVar parameter = retrieved.params().iterator().next();
        assertEquals(parameterRef.binaryName(), parameter.type().binaryName());
        assertEquals(1, retrieved.annotations().size());
        assertEquals(JsOverlay.class.getCanonicalName(), retrieved.annotations().iterator().next().getAnnotationClass().binaryName());
    }

    private void commonAddGetterMethod(JMethod retrieved, JClass propertyRef, String publicPropertyName, int expectedMods, Class<?> expectedAnnotation) {
        assertEquals(expectedMods, retrieved.mods().getValue());
        assertEquals("get" + publicPropertyName, retrieved.name());
        assertEquals(propertyRef, retrieved.type());
        assertTrue(retrieved.params().isEmpty());
        assertEquals(1, retrieved.annotations().size());
        final JAnnotationUse annotation = retrieved.annotations().iterator().next();
        assertEquals(expectedAnnotation.getCanonicalName(), annotation.getAnnotationClass().binaryName());
    }

    private void commonAddSetterMethod(JMethod retrieved, JClass parameterType, String publicPropertyName, String privatePropertyName, int expectedMods, Class<?> expectedAnnotation) {
        commonParamMethod("set", retrieved, parameterType, publicPropertyName, privatePropertyName + "Param", expectedMods, expectedAnnotation);
    }

    private void commonParamMethod(String methodNamePrefix, JMethod retrieved, JType parameterType, String publicPropertyName, String parameterName, int expectedMods, Class<?> expectedAnnotation) {
        assertEquals(expectedMods, retrieved.mods().getValue());
        assertEquals(methodNamePrefix + publicPropertyName, retrieved.name());
        assertEquals(Void.TYPE.getCanonicalName(), retrieved.type().binaryName());
        assertEquals(1, retrieved.params().size());
        assertEquals(parameterName, retrieved.params().get(0).name());
        assertEquals(parameterType.binaryName(), retrieved.params().get(0).type().binaryName());
        assertEquals(1, retrieved.annotations().size());
        assertEquals(expectedAnnotation.getCanonicalName(), retrieved.annotations().iterator().next().getAnnotationClass().binaryName());
    }

    private void commonVarargMethod(String methodNamePrefix, JMethod retrieved, JClass parameterType, String publicPropertyName, String parameterName, int expectedMods, Class<?> expectedAnnotation) {
        assertEquals(expectedMods, retrieved.mods().getValue());
        assertEquals(methodNamePrefix + publicPropertyName, retrieved.name());
        assertEquals(Void.TYPE.getCanonicalName(), retrieved.type().binaryName());
        assertNotNull(retrieved.listVarParam());
        assertEquals(parameterName, retrieved.listVarParam().name());
        assertEquals(parameterType.binaryName(), retrieved.listVarParam().type().binaryName());
        assertEquals(1, retrieved.annotations().size());
        assertEquals(expectedAnnotation.getCanonicalName(), retrieved.annotations().iterator().next().getAnnotationClass().binaryName());
    }
}