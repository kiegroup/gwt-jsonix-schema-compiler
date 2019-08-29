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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;
import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
import org.hisrc.jsonix.settings.LogLevelSetting;

import static gwt.jsonix.marshallers.xjc.plugin.builders.BuilderUtils.log;

public class JsUtilsBuilder {

    private static final String GENERIC_TYPE_NAME = "D";
    private static final String ELEMENT = "element";
    private static final int PUBLIC_STATIC_MODS = JMod.PUBLIC + JMod.STATIC;
    private static final int PUBLIC_STATIC_NATIVE_MODS = PUBLIC_STATIC_MODS + JMod.NATIVE;

    private static final String GET_UNWRAPPED_ELEMENTS_ARRAY_METHOD = "\r\npublic static native <D> JsArrayLike<D> getUnwrappedElementsArray(final JsArrayLike<D> original) /*-{\n" +
            "        var toReturn = original.map(function (arrayItem) {\n" +
            "            var retrieved = arrayItem.value\n" +
            "            var toSet = retrieved == null ? arrayItem : retrieved\n" +
            "            console.log(toSet);\n" +
            "            return toSet;\n" +
            "        });\n" +
            "        return toReturn;\n" +
            "    }-*/;\n";

    private static final String GET_UNWRAPPED_ELEMENT_METHOD = "\r\npublic static native Object getUnwrappedElement(final Object original) /*-{\n" +
            "        var toReturn = original.value;\n" +
            "        var toSet = toReturn == null ? original : toReturn;\n" +
            "        console.log(toSet);\n" +
            "        return toSet;\n" +
            "    }-*/;\n";

    private JsUtilsBuilder() {
    }

    public static JDefinedClass generateJsUtilsClass(JCodeModel jCodeModel) throws JClassAlreadyExistsException {
        final JDefinedClass toReturn = getJsUtilsClass(jCodeModel);
        populateJsUtilClass(jCodeModel, toReturn);
        return toReturn;
    }

    protected static void populateJsUtilClass(JCodeModel jCodeModel, JDefinedClass toPopulate) {
        addEmptyConstructor(toPopulate);
        JMethod addMethod = addAddMethod(jCodeModel, toPopulate);
        addAddAllMethod(jCodeModel, toPopulate, addMethod);
        addRemoveMethod(jCodeModel, toPopulate);
        addToListMethod(jCodeModel, toPopulate);
        addGetUnwrappedElementsArrayMethod(toPopulate);
        addGetUnwrappedElementMethod(toPopulate);
    }

    protected static JDefinedClass getJsUtilsClass(JCodeModel jCodeModel) throws JClassAlreadyExistsException {
        log(LogLevelSetting.DEBUG, "Creating  JsUtils class");
        final JDefinedClass toReturn = jCodeModel._class("JsUtils");
        JDocComment comment = toReturn.javadoc();
        comment.append("Utility class to provide generic methods used by all specific JSInterop classes");
        return toReturn;
    }

    protected static void addEmptyConstructor(JDefinedClass jsUtils) {
        log(LogLevelSetting.DEBUG, "Add empty constructor...");
        jsUtils.constructor(JMod.PRIVATE).body().directStatement(" //Private constructor to prevent instantiation");
    }

    protected static JMethod addAddMethod(JCodeModel jCodeModel, JDefinedClass jsUtils) {
        log(LogLevelSetting.DEBUG, "Add 'add' method...");
        JClass genericT = getGenericT(jCodeModel);
        final JMethod toReturn = getGenerifiedJMethod(jsUtils, Void.TYPE, "add");
        final JVar jsArrayLikeParameter = getJSArrayNarrowedJVar(jCodeModel, toReturn);
        final JVar elementParam = toReturn.param(JMod.FINAL, genericT, ELEMENT);
        final JBlock block = toReturn.body();
        final JVar length = block.decl(jCodeModel.INT, "length");
        length.init(JExpr.invoke(jsArrayLikeParameter, "getLength"));
        block.add(jsArrayLikeParameter.invoke("setLength").arg(length.plus(JExpr.lit(1))));
        block.add(jsArrayLikeParameter.invoke("setAt").arg(length).arg(elementParam));
        return toReturn;
    }

    protected static JMethod addAddAllMethod(JCodeModel jCodeModel, JDefinedClass jsUtils, JMethod addMethod) {
        log(LogLevelSetting.DEBUG, "Add 'addAll' method...");
        JClass genericT = getGenericT(jCodeModel);
        final JMethod toReturn = getGenerifiedJMethod(jsUtils, Void.TYPE, "addAll");
        final JVar jsArrayLikeParameter = getJSArrayNarrowedJVar(jCodeModel, toReturn);
        final JVar elementParam = toReturn.varParam(genericT, "elements");
        elementParam.mods().setFinal(true);
        final JBlock block = toReturn.body();
        final JInvocation addInvocation = jsUtils.staticInvoke(addMethod).arg(jsArrayLikeParameter).arg(JExpr.ref(ELEMENT));
        final JForEach forEachElement = block.forEach(genericT, ELEMENT, elementParam);
        forEachElement.body().add(addInvocation);
        return toReturn;
    }

    protected static JMethod addRemoveMethod(JCodeModel jCodeModel, JDefinedClass jsUtils) {
        log(LogLevelSetting.DEBUG, "Add 'remove' method...");
        final JMethod toReturn = getGenerifiedJMethod(jsUtils, Void.TYPE, "remove");
        final JVar jsArrayLikeParameter = getJSArrayNarrowedJVar(jCodeModel, toReturn);
        final JVar indexParam = toReturn.param(JMod.FINAL, jCodeModel.INT, "index");
        final JBlock block = toReturn.body();
        final JVar targetIndex = block.decl(jCodeModel.INT, "targetIndex", JExpr.lit(0));
        final JForLoop jForLoop = block._for();
        final JVar sourceIndex = jForLoop.init(jCodeModel.INT, "sourceIndex", JExpr.lit(0));
        jForLoop.test(sourceIndex.lt(jsArrayLikeParameter.invoke("getLength")));
        jForLoop.update(sourceIndex.incr());
        final JConditional jConditional = jForLoop.body()._if(sourceIndex.ne(indexParam));
        jConditional._then()
                .add(jsArrayLikeParameter.invoke("setAt")
                             .arg(targetIndex.incr())
                             .arg(jsArrayLikeParameter.invoke("getAt").arg(sourceIndex)));
        block.add(jsArrayLikeParameter.invoke("setLength").arg(targetIndex));
        return toReturn;
    }

    protected static JMethod addToListMethod(JCodeModel jCodeModel, JDefinedClass jsUtils) {
        log(LogLevelSetting.DEBUG, "Add 'toList' method...");
        JClass rawListClass = jCodeModel.ref(List.class);
        final JClass genericT = getGenericT(jCodeModel);
        JClass narrowedList = rawListClass.narrow(genericT);
        JClass rawArrayListClass = jCodeModel.ref(ArrayList.class);
        JClass arrayListField = rawArrayListClass.narrow(genericT);

        final JMethod toReturn = getGenerifiedJMethod(jsUtils, narrowedList, "toList");
        final JVar jsArrayLikeParameter = getJSArrayNarrowedJVar(jCodeModel, toReturn);
        final JBlock block = toReturn.body();
        final JVar listToReturn = block.decl(JMod.FINAL, narrowedList, "toReturn", JExpr._new(arrayListField));
        final JConditional nonNull = block._if(jCodeModel.ref(Objects.class).staticInvoke("nonNull").arg(jsArrayLikeParameter));
        final JForLoop jForLoop = nonNull._then()._for();
        final JVar i = jForLoop.init(jCodeModel.INT, "i", JExpr.lit(0));
        jForLoop.test(i.lt(jsArrayLikeParameter.invoke("getLength")));
        jForLoop.update(i.incr());
        final JBlock forLoopBody = jForLoop.body();
        final JVar toAdd = forLoopBody.decl(JMod.FINAL, genericT, "toAdd", jCodeModel.ref(Js.class).staticInvoke("uncheckedCast").arg(jsArrayLikeParameter.invoke("getAt").arg(i)));
        forLoopBody.add(listToReturn.invoke("add").arg(toAdd));
        block._return(listToReturn);
        return toReturn;
    }

    protected static void addGetUnwrappedElementsArrayMethod(JDefinedClass jsUtils) {
        log(LogLevelSetting.DEBUG, "Add 'getUnwrappedElementsArray' method...");
        jsUtils.direct(GET_UNWRAPPED_ELEMENTS_ARRAY_METHOD);
    }

    protected static void addGetUnwrappedElementMethod(JDefinedClass jsUtils) {
        log(LogLevelSetting.DEBUG, "Add 'getUnwrappedElement' method...");
        jsUtils.direct(GET_UNWRAPPED_ELEMENT_METHOD);
    }

    private static JMethod getGenerifiedJMethod(JDefinedClass jsUtils, Class<?> returnType, String methodName) {
        JMethod toReturn = getJMethod(jsUtils, returnType, methodName);
        toReturn.generify(GENERIC_TYPE_NAME);
        return toReturn;
    }

    private static JMethod getGenerifiedJMethod(JDefinedClass jsUtils, JClass returnType, String methodName) {
        JMethod toReturn = getJMethod(jsUtils, returnType, methodName);
        toReturn.generify(GENERIC_TYPE_NAME);
        return toReturn;
    }

    private static JMethod getGenerifiedNativeJMethod(JDefinedClass jsUtils, Class<?> returnType, String methodName) {
        JMethod toReturn = getNativeJMethod(jsUtils, returnType, methodName);
        toReturn.generify(GENERIC_TYPE_NAME);
        return toReturn;
    }

    private static JMethod getGenerifiedNativeJMethod(JDefinedClass jsUtils, JClass returnType, String methodName) {
        JMethod toReturn = getNativeJMethod(jsUtils, returnType, methodName);
        toReturn.generify(GENERIC_TYPE_NAME);
        return toReturn;
    }

    private static JMethod getJMethod(JDefinedClass jsUtils, Class<?> returnType, String methodName) {
        return jsUtils.method(PUBLIC_STATIC_MODS, returnType, methodName);
    }

    private static JMethod getJMethod(JDefinedClass jsUtils, JClass returnType, String methodName) {
        return jsUtils.method(PUBLIC_STATIC_MODS, returnType, methodName);
    }

    private static JMethod getNativeJMethod(JDefinedClass jsUtils, Class<?> returnType, String methodName) {
        return jsUtils.method(PUBLIC_STATIC_NATIVE_MODS, returnType, methodName);
    }

    private static JMethod getNativeJMethod(JDefinedClass jsUtils, JClass returnType, String methodName) {
        return jsUtils.method(PUBLIC_STATIC_NATIVE_MODS, returnType, methodName);
    }

    private static JVar getJSArrayNarrowedJVar(JCodeModel jCodeModel, JMethod jmethod) {
        return jmethod.param(JMod.FINAL, getJsArrayNarrowedClass(jCodeModel), "jsArrayLike");
    }

    private static JClass getGenericT(JCodeModel jCodeModel) {
        return jCodeModel.ref(GENERIC_TYPE_NAME);
    }

    private static JClass getJsArrayNarrowedClass(JCodeModel jCodeModel) {
        JClass jsArrayLikeClass = jCodeModel.ref(JsArrayLike.class);
        return jsArrayLikeClass.narrow(getGenericT(jCodeModel));
    }
}
