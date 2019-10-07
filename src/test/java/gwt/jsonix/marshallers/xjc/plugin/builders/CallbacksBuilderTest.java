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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import gwt.jsonix.marshallers.xjc.plugin.AbstractBuilderTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static gwt.jsonix.marshallers.xjc.plugin.utils.BuilderUtils.MARSHALL_CALLBACK;
import static gwt.jsonix.marshallers.xjc.plugin.utils.BuilderUtils.UNMARSHALL_CALLBACK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CallbacksBuilderTest extends AbstractBuilderTest {

    @Test
    public void generateJSInteropCallbacks() throws JClassAlreadyExistsException {
        JDefinedClass otherJDefinedClass = jCodeModel._class("foo.bar.OtherClass");
        List<JDefinedClass> containersClasses = Arrays.asList(referredJDefinedClass, otherJDefinedClass);
        final Map<String, Map<String, JDefinedClass>> retrieved = CallbacksBuilder.generateJSInteropCallbacks(containersClasses, jCodeModel);
        assertNotNull(retrieved);
        assertEquals(containersClasses.size(), retrieved.size());
        containersClasses.forEach(jDefinedClass -> {
            assertTrue(retrieved.containsKey(jDefinedClass.name()));
            final Map<String, JDefinedClass> stringJDefinedClassMap = retrieved.get(jDefinedClass.name());
            assertNotNull(stringJDefinedClassMap);
            assertEquals(2, stringJDefinedClassMap.size());
            commonVerifyUnMarshallCallback(stringJDefinedClassMap.get("UnmarshallCallback"), jDefinedClass);
            commonVerifyMarshallCallback(stringJDefinedClassMap.get("MarshallCallback"), jDefinedClass.name());
        });
    }

    @Test
    public void createUnMarshallCallback() throws JClassAlreadyExistsException {
        JDefinedClass retrieved = CallbacksBuilder.createUnMarshallCallback(jCodeModel, referredJDefinedClass, "base.package");
        commonVerifyUnMarshallCallback(retrieved, referredJDefinedClass);
    }

    @Test
    public void createMarshallCallback() throws JClassAlreadyExistsException {
        JDefinedClass retrieved = CallbacksBuilder.createMarshallCallback(jCodeModel, "CONTAINER", "base.package");
        commonVerifyMarshallCallback(retrieved, "CONTAINER");
    }

    @Test
    public void createCallback() throws JClassAlreadyExistsException {
        JDefinedClass retrieved = CallbacksBuilder.createCallback(jCodeModel, "CallbackName", "CommentString", parameterRef, "parameterName", "base.package");
        assertNotNull(retrieved);
        commonVerifyCallMethod(retrieved, parameterRef, "parameterName");
    }

    @Test
    public void addCallEventMethod() {
        CallbacksBuilder.addCallEventMethod(jDefinedClass, parameterRef, "refClass");
        commonVerifyCallMethod(jDefinedClass, parameterRef, "refClass");
    }

    private void commonVerifyUnMarshallCallback(JDefinedClass toVerify, JDefinedClass usedReferredJDefinedClass) {
        assertNotNull(toVerify);
        assertEquals(usedReferredJDefinedClass.name() + UNMARSHALL_CALLBACK, toVerify.name());
        commonVerifyCallMethod(toVerify, usedReferredJDefinedClass, StringUtils.uncapitalize(usedReferredJDefinedClass.name()));
    }

    private void commonVerifyMarshallCallback(JDefinedClass toVerify, String originalClassName) {
        assertNotNull(toVerify);
        assertEquals(originalClassName + MARSHALL_CALLBACK, toVerify.name());
        commonVerifyCallMethod(toVerify, jCodeModel.ref(String.class), "xmlString");
    }

    private void commonVerifyCallMethod(JDefinedClass toVerify, JClass parameterRefUsed, String refClass) {
        final Optional<JMethod> optionalRetrieved = toVerify.methods().stream().filter(jMethod -> Objects.equals("callEvent", jMethod.name())).findFirst();
        assertTrue(optionalRetrieved.isPresent());
        JMethod retrieved = optionalRetrieved.get();
        assertEquals(1, retrieved.params().size());
        assertEquals(parameterRefUsed, retrieved.params().get(0).type());
        assertEquals(refClass, retrieved.params().get(0).name());
    }
}