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
package gwt.jsonix.marshallers.xjc.plugin;

import java.util.List;
import java.util.Map;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.apache.commons.lang3.StringUtils;

import static gwt.jsonix.marshallers.xjc.plugin.BuilderUtils.MAIN_JS;
import static gwt.jsonix.marshallers.xjc.plugin.BuilderUtils.MARSHALL_CALLBACK;
import static gwt.jsonix.marshallers.xjc.plugin.BuilderUtils.UNMARSHALL_CALLBACK;

/**
 * Actual builder for the <b>JSInterop</b> <code>MainJs</code> class
 */
public class MainJsBuilder {

    /**
     * Method to create the <b>JSInterop</b> <code>MainJs</code> class
     * @param callbacksMap
     * @param containersClasses
     * @param jCodeModel
     * @throws Exception
     */
    public static void generateJSInteropMainJs(final Map<String, Map<String, JDefinedClass>> callbacksMap, List<JDefinedClass> containersClasses, JCodeModel jCodeModel) throws Exception {
        for (JDefinedClass mainObject : containersClasses) {
            populateJCodeModel(jCodeModel, mainObject, callbacksMap.get(mainObject.name()));
        }
    }

    protected static void populateJCodeModel(JCodeModel toPopulate, JClass containerRef, Map<String, JDefinedClass> callbackMap) throws Exception {
        String basePackage = containerRef._package().name();
        if (basePackage.contains(".")) {
            basePackage = basePackage.substring(0, basePackage.lastIndexOf("."));
        }
        String fullMainJsName = basePackage + "." + MAIN_JS;
        JDefinedClass jDefinedClass = toPopulate._getClass(fullMainJsName);
        if (jDefinedClass == null) {
            jDefinedClass = toPopulate._class(basePackage + "." + MAIN_JS);
            JDocComment comment = jDefinedClass.javadoc();
            String commentString = "JSInterop adapter to use for marshalling/unmarshalling.";
            comment.append(commentString);
            jDefinedClass.annotate(toPopulate.ref(JsType.class)).param("isNative", true).param("namespace", toPopulate.ref(JsPackage.class).staticRef("GLOBAL"));
        }
        addUnmarshall(toPopulate, jDefinedClass, callbackMap.get(UNMARSHALL_CALLBACK));
        addMarshall(toPopulate, jDefinedClass, containerRef, callbackMap.get(MARSHALL_CALLBACK));
    }

    protected static void addUnmarshall(JCodeModel toPopulate, JDefinedClass jDefinedClass, JClass callbackRef) {
        String unmarshallMethodName = "unmarshall";
        JClass firstParameterRef = toPopulate.ref(String.class);
        String firstParameterName = "xmlString";
        addCallbackMethod(toPopulate, jDefinedClass, unmarshallMethodName, firstParameterRef,
                          firstParameterName, callbackRef);
    }

    protected static void addMarshall(JCodeModel toPopulate, JDefinedClass jDefinedClass, JClass firstParameterRef, JClass callbackRef) {
        String marshallMethodName = "marshall";
        String firstParameterName = StringUtils.uncapitalize(firstParameterRef.name());
        addCallbackMethod(toPopulate, jDefinedClass, marshallMethodName, firstParameterRef,
                          firstParameterName, callbackRef);
    }

    /**
     * @param toPopulate
     * @param jDefinedClass
     * @param methodName
     * @param firstParameterRef
     * @param firstParameterName
     * @param callbackRef
     */
    protected static void addCallbackMethod(JCodeModel toPopulate, JDefinedClass jDefinedClass, String methodName,
                                            JClass firstParameterRef,
                                            String firstParameterName,
                                            JClass callbackRef) {
        int mod = JMod.PUBLIC + JMod.FINAL + JMod.STATIC + JMod.NATIVE;
        String callbackPropertyName = StringUtils.uncapitalize(callbackRef.name());
        JMethod method = jDefinedClass.method(mod, Void.TYPE, methodName);
        method.param(firstParameterRef, firstParameterName);
        method.param(callbackRef, callbackPropertyName);
        method.annotate(toPopulate.ref(JsMethod.class));
    }
}
