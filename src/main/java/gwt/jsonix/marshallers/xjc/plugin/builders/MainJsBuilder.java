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

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
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
import gwt.jsonix.marshallers.xjc.plugin.dtos.ConstructorMapper;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;
import org.apache.commons.lang3.StringUtils;

import static gwt.jsonix.marshallers.xjc.plugin.utils.BuilderUtils.MAIN_JS;
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
     * @param callbacksMap
     * @param containersClasses
     * @param jCodeModel
     * @throws Exception
     */
    public static void generateJSInteropMainJs(final Map<String, Map<String, JDefinedClass>> callbacksMap, final List<JDefinedClass> containersClasses, final Map<String, List<ConstructorMapper>> constructorsMap, JCodeModel jCodeModel) throws JClassAlreadyExistsException {
        if (containersClasses.isEmpty()) {
            return;
        }
        String basePackage = containersClasses.get(0)._package().name();
        if (basePackage.contains(".")) {
            basePackage = basePackage.substring(0, basePackage.lastIndexOf('.'));
        }
        final JDefinedClass mainJsClass = getMainJsClass(jCodeModel, basePackage);
        final JMethod getJSONObjectMethod = addGetJSONObjectMethod(mainJsClass, jCodeModel);
        addGetConstructorsMap(constructorsMap, mainJsClass, getJSONObjectMethod, jCodeModel);
        addInitializeJsInteropConstructors(mainJsClass, jCodeModel);

        for (JDefinedClass mainObject : containersClasses) {
            populateJCodeModel(jCodeModel, mainObject, mainJsClass, callbacksMap.get(mainObject.name()));
        }
    }

    protected static void addInitializeJsInteropConstructors(final JDefinedClass mainJsClass,
                                                             final JCodeModel jCodeModel) {
        final int mod = JMod.PUBLIC + JMod.FINAL + JMod.STATIC + JMod.NATIVE;
        final JMethod method = mainJsClass.method(mod, Void.TYPE, "initializeJsInteropConstructors");
        method.param(JsPropertyMap.class, "constructorsMap");
        method.annotate(jCodeModel.ref(JsMethod.class));
    }

    protected static void addGetConstructorsMap(final Map<String, List<ConstructorMapper>> constructorsMap, final JDefinedClass mainJsClass,
                                                JMethod getJSONObjectMethod, final JCodeModel jCodeModel) {
        final int mod = JMod.PUBLIC + JMod.STATIC;
        final JMethod method = mainJsClass.method(mod, JsPropertyMap.class, "getConstructorsMap");
        method.annotate(jCodeModel.ref(JsOverlay.class));
        final JBlock body = method.body();
        final JClass jsPropertyMapRef = jCodeModel.ref(JsPropertyMap.class);
        final JClass jsonObjectRef = jCodeModel.ref(JSONObject.class);
        final JVar toReturnVar = body.decl(JMod.FINAL, jsPropertyMapRef, "toReturn", jsPropertyMapRef.staticInvoke("of"));
        AtomicInteger size = new AtomicInteger(0);
        constructorsMap.values().forEach(constructorMappers -> size.addAndGet(constructorMappers.size()));
        final JArray jArray = JExpr.newArray(jsonObjectRef);
        constructorsMap.forEach((baseType, constructorMappers) -> constructorMappers.forEach(constructorMapper -> {
            JExpression originalTypeNameExpression = constructorMapper.getOriginalTypeName() == null ? JExpr._null() : JExpr.lit(constructorMapper.getOriginalTypeName());
            JExpression nameSpaceExpression = constructorMapper.getNameSpace() == null ? JExpr._null() : JExpr.lit(constructorMapper.getNameSpace());
            jArray.add(JExpr.invoke(getJSONObjectMethod).arg(JExpr.lit(constructorMapper.getJsiTypeName())).arg(originalTypeNameExpression).arg(nameSpaceExpression));
        }));
        final JVar jsonObjectArrayVar = body.decl(JMod.FINAL, jsonObjectRef.array(), "toSet", jArray);
        body.add(toReturnVar.invoke("set").arg("constructors").arg(jsonObjectArrayVar));
        body._return(toReturnVar);
    }

    protected static JMethod addGetJSONObjectMethod(final JDefinedClass mainJsClass,
                                                    final JCodeModel jCodeModel) {
        final int mod = JMod.PRIVATE + JMod.STATIC;
        final JMethod toReturn = mainJsClass.method(mod, JSONObject.class, "getJSONObjectMethod");
        toReturn.annotate(jCodeModel.ref(JsOverlay.class));
        final JVar nameParam = toReturn.param(String.class, "name");
        final JVar typeNameParam = toReturn.param(String.class, "typeName");
        final JVar nameSpaceParam = toReturn.param(String.class, "nameSpace");

        final JBlock body = toReturn.body();
        final JClass jsonObjectRef = jCodeModel.ref(JSONObject.class);
        final JClass jsonStringRef = jCodeModel.ref(JSONString.class);

        final JVar toReturnVar = body.decl(JMod.FINAL, jsonObjectRef, "toReturn", JExpr._new(jsonObjectRef));
        body.add(toReturnVar.invoke("put").arg("name").arg(JExpr._new(jsonStringRef).arg(nameParam)));
        body._if(typeNameParam.ne(JExpr._null()))._then().add(toReturnVar.invoke("put").arg("typeName").arg(JExpr._new(jsonStringRef).arg(typeNameParam)));
        body._if(nameSpaceParam.ne(JExpr._null()))._then().add(toReturnVar.invoke("put").arg("nameSpace").arg(JExpr._new(jsonStringRef).arg(nameSpaceParam)));
        body._return(toReturnVar);
        return toReturn;
    }

    protected static void populateJCodeModel(JCodeModel toPopulate, JClass containerRef, JDefinedClass mainJsClass, Map<String, JDefinedClass> callbackMap) {
        addUnmarshall(toPopulate, mainJsClass, callbackMap.get(UNMARSHALL_CALLBACK));
        addMarshall(toPopulate, mainJsClass, containerRef, callbackMap.get(MARSHALL_CALLBACK));
    }

    protected static JDefinedClass getMainJsClass(final JCodeModel toPopulate, String basePackage) throws JClassAlreadyExistsException {
        String fullMainJsName = basePackage + "." + MAIN_JS;
        JDefinedClass jDefinedClass = toPopulate._getClass(fullMainJsName);

        if (jDefinedClass == null) {
            jDefinedClass = toPopulate._class(basePackage + "." + MAIN_JS);
            JDocComment comment = jDefinedClass.javadoc();
            String commentString = "JSInterop adapter to use for marshalling/unmarshalling.";
            comment.append(commentString);
            jDefinedClass.annotate(toPopulate.ref(JsType.class)).param("isNative", true).param("namespace", toPopulate.ref(JsPackage.class).staticRef("GLOBAL"));
        }
        return jDefinedClass;
    }

    protected static void addUnmarshall(JCodeModel toPopulate, JDefinedClass mainJsClass, JClass callbackRef) {
        String unmarshallMethodName = "unmarshall";
        JClass firstParameterRef = toPopulate.ref(String.class);
        String firstParameterName = "xmlString";
        String secondParameterName = "dynamicNamespace";
        JClass secondParameterRef = toPopulate.ref(String.class);
        addCallbackMethod(toPopulate, mainJsClass, unmarshallMethodName, firstParameterRef,
                          firstParameterName, secondParameterRef, secondParameterName, callbackRef);
    }

    protected static void addMarshall(JCodeModel toPopulate, JDefinedClass mainJsClass, JClass firstParameterRef, JClass callbackRef) {
        String marshallMethodName = "marshall";
        String firstParameterName = StringUtils.uncapitalize(firstParameterRef.name());
        String secondParameterName = "dynamicNamespace";
        JClass secondParameterRef = toPopulate.ref(String.class);
        addCallbackMethod(toPopulate, mainJsClass, marshallMethodName, firstParameterRef,
                          firstParameterName, secondParameterRef, secondParameterName, callbackRef);
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
                                            JClass secondParameterRef,
                                            String secondParameterName,
                                            JClass callbackRef) {
        int mod = JMod.PUBLIC + JMod.FINAL + JMod.STATIC + JMod.NATIVE;
        String callbackPropertyName = StringUtils.uncapitalize(callbackRef.name());
        JMethod method = jDefinedClass.method(mod, Void.TYPE, methodName);
        method.param(firstParameterRef, firstParameterName);
        method.param(secondParameterRef, secondParameterName);
        method.param(callbackRef, callbackPropertyName);
        method.annotate(toPopulate.ref(JsMethod.class));
    }
}
