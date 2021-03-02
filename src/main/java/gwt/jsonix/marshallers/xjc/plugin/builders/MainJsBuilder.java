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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JArray;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JInvocation;
import gwt.jsonix.marshallers.xjc.plugin.dtos.ConstructorMapper;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;
import org.apache.commons.lang3.StringUtils;

import static gwt.jsonix.marshallers.xjc.plugin.JsonixGWTPlugin.MAIN_JS;
import static gwt.jsonix.marshallers.xjc.plugin.utils.BuilderUtils.MARSHALL_CALLBACK;
import static gwt.jsonix.marshallers.xjc.plugin.utils.BuilderUtils.UNMARSHALL_CALLBACK;

/**
 * Actual builder for the <b>JSInterop</b> <code>MainJs</code> class
 */
public class MainJsBuilder {

    private MainJsBuilder() {
    }

    /**
     * Method to create the <b>JSInterop</b> <code>MainJs</code> class
     *
     * @param callbacksMap
     * @param containersClasses
     * @param constructorsMap
     * @param jCodeModel
     * @param mainJsName
     * @throws JClassAlreadyExistsException
     */
    public static void generateJSInteropMainJs(final Map<String, Map<String, JDefinedClass>> callbacksMap,
                                               final List<JDefinedClass> containersClasses,
                                               final Map<String, List<ConstructorMapper>> constructorsMap,
                                               final JCodeModel jCodeModel,
                                               final String mainJsName) throws JClassAlreadyExistsException {
        if (containersClasses.isEmpty()) {
            return;
        }
        String basePackage = containersClasses.get(0)._package().name();
        if (basePackage.contains(".")) {
            basePackage = basePackage.substring(0, basePackage.lastIndexOf('.'));
        }
        final JDefinedClass mainJsClass = getMainJsClass(jCodeModel, basePackage, mainJsName);
        for (JDefinedClass mainObject : containersClasses) {
            populateJCodeModel(jCodeModel, mainObject, mainJsClass, callbacksMap.get(mainObject.name()));
        }
    }

    /**
     *
     * @param toPopulate
     * @param containerRef
     * @param mainJsClass
     * @param callbackMap
     */
    protected static void populateJCodeModel(final JCodeModel toPopulate,
                                             final JClass containerRef,
                                             final JDefinedClass mainJsClass,
                                             final Map<String, JDefinedClass> callbackMap) {
        addUnmarshall(toPopulate, mainJsClass, callbackMap.get(UNMARSHALL_CALLBACK));
        addMarshall(toPopulate, mainJsClass, containerRef, callbackMap.get(MARSHALL_CALLBACK));
    }

    /**
     *
     * @param toPopulate
     * @param basePackage
     * @param mainJsName
     * @return
     * @throws JClassAlreadyExistsException
     */
    protected static JDefinedClass getMainJsClass(final JCodeModel toPopulate,
                                                  final String basePackage,
                                                  final String mainJsName) throws JClassAlreadyExistsException {
        String fullMainJsName = basePackage + "." + mainJsName;
        JDefinedClass jDefinedClass = toPopulate._getClass(fullMainJsName);
        if (jDefinedClass == null) {
            jDefinedClass = toPopulate._class(fullMainJsName);
            JDocComment comment = jDefinedClass.javadoc();
            String commentString = "JSInterop adapter to use for marshalling/unmarshalling.";
            comment.append(commentString);
            final JAnnotationUse classAnnotation = jDefinedClass.annotate(toPopulate.ref(JsType.class))
                    .param("isNative", true)
                    .param("namespace", toPopulate.ref(JsPackage.class).staticRef("GLOBAL"));
            if (!MAIN_JS.equals(mainJsName)) {
                classAnnotation.param("name", mainJsName);
            }
        }
        return jDefinedClass;
    }

    /**
     *
     * @param toPopulate
     * @param mainJsClass
     * @param callbackRef
     */
    protected static void addUnmarshall(final JCodeModel toPopulate,
                                        final JDefinedClass mainJsClass,
                                        final JClass callbackRef) {
        String unmarshallMethodName = "unmarshall";
        JClass firstParameterRef = toPopulate.ref(String.class);
        String firstParameterName = "xmlString";
        String secondParameterName = "dynamicNamespace";
        JClass secondParameterRef = toPopulate.ref(String.class);
        addCallbackMethod(toPopulate, mainJsClass, unmarshallMethodName, firstParameterRef,
                          firstParameterName, secondParameterRef, secondParameterName, callbackRef);
    }

    /**
     *
     * @param toPopulate
     * @param mainJsClass
     * @param firstParameterRef
     * @param callbackRef
     */
    protected static void addMarshall(final JCodeModel toPopulate,
                                      final JDefinedClass mainJsClass,
                                      final JClass firstParameterRef,
                                      final JClass callbackRef) {
        String marshallMethodName = "marshall";
        String firstParameterName = StringUtils.uncapitalize(firstParameterRef.name());
        String secondParameterName = "namespaces";
        JClass secondParameterRef = toPopulate.ref(JavaScriptObject.class);
        addCallbackMethod(toPopulate, mainJsClass, marshallMethodName, firstParameterRef,
                          firstParameterName, secondParameterRef, secondParameterName, callbackRef);
    }

    /**
     *
     * @param toPopulate
     * @param jDefinedClass
     * @param methodName
     * @param firstParameterRef
     * @param firstParameterName
     * @param callbackRef
     */
    protected static void addCallbackMethod(final JCodeModel toPopulate,
                                            final JDefinedClass jDefinedClass,
                                            final String methodName,
                                            final JClass firstParameterRef,
                                            final String firstParameterName,
                                            final JClass callbackRef) {
        int mod = JMod.PUBLIC + JMod.FINAL + JMod.STATIC + JMod.NATIVE;
        String callbackPropertyName = StringUtils.uncapitalize(callbackRef.name());
        JMethod method = jDefinedClass.method(mod, Void.TYPE, methodName);
        method.param(firstParameterRef, firstParameterName);
        method.param(callbackRef, callbackPropertyName);
        method.annotate(toPopulate.ref(JsMethod.class));
    }

    /**
     *
     * @param toPopulate
     * @param jDefinedClass
     * @param methodName
     * @param firstParameterRef
     * @param firstParameterName
     * @param secondParameterRef
     * @param secondParameterName
     * @param callbackRef
     */
    protected static void addCallbackMethod(final JCodeModel toPopulate,
                                            final JDefinedClass jDefinedClass,
                                            final String methodName,
                                            final JClass firstParameterRef,
                                            final String firstParameterName,
                                            final JClass secondParameterRef,
                                            final String secondParameterName,
                                            final JClass callbackRef) {
        int mod = JMod.PUBLIC + JMod.FINAL + JMod.STATIC + JMod.NATIVE;
        String callbackPropertyName = StringUtils.uncapitalize(callbackRef.name());
        JMethod method = jDefinedClass.method(mod, Void.TYPE, methodName);
        method.param(firstParameterRef, firstParameterName);
        method.param(secondParameterRef, secondParameterName);
        method.param(callbackRef, callbackPropertyName);
        method.annotate(toPopulate.ref(JsMethod.class));
    }
}
