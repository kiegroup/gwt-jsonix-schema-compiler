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

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;
import jsinterop.base.JsArrayLike;
import org.hisrc.jsonix.settings.LogLevelSetting;

import static gwt.jsonix.marshallers.xjc.plugin.builders.BuilderUtils.log;

public class JsUtilsBuilder {

    private static final String GENERIC_TYPE_NAME = "D";
    private static final String ELEMENT = "element";

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
        int mods = JMod.PUBLIC + JMod.STATIC;
        final JMethod toReturn = jsUtils.method(mods, Void.TYPE, "add");
        toReturn.generify(GENERIC_TYPE_NAME);
        final JVar jsArrayLikeParameter = getJSArrayNarrowedJVar(jCodeModel, toReturn);
        final JVar elementParam = toReturn.param(JMod.FINAL, genericT, ELEMENT);
        final JBlock block = toReturn.body();
        final JVar length = block.decl(jCodeModel.INT, "length");
        length.init(JExpr.invoke(jsArrayLikeParameter, "getLength"));
        block.add(jsArrayLikeParameter.invoke("setLength").arg(length.plus(JExpr.lit(1))));
        block.add(jsArrayLikeParameter.invoke("setAt").arg(length).arg(elementParam));
        return toReturn;
    }

    protected static void addAddAllMethod(JCodeModel jCodeModel, JDefinedClass jsUtils, JMethod addMethod) {
        log(LogLevelSetting.DEBUG, "Add 'addAll' method...");
        JClass genericT = getGenericT(jCodeModel);
        int mods = JMod.PUBLIC + JMod.STATIC;
        final JMethod add = jsUtils.method(mods, Void.TYPE, "addAll");
        add.generify(GENERIC_TYPE_NAME);
        final JVar jsArrayLikeParameter = getJSArrayNarrowedJVar(jCodeModel, add);
        final JVar elementParam = add.varParam(genericT, "elements");
        elementParam.mods().setFinal(true);
        final JBlock block = add.body();
        final JInvocation addInvocation = jsUtils.staticInvoke(addMethod).arg(jsArrayLikeParameter).arg(JExpr.ref(ELEMENT));
        final JForEach forEachElement = block.forEach(genericT, ELEMENT, elementParam);
        forEachElement.body().add(addInvocation);
    }

    private static JVar getJSArrayNarrowedJVar(JCodeModel jCodeModel, JMethod jmethod) {
        return jmethod.param(JMod.FINAL, getJsArrayNarrowedClass(jCodeModel), "jsArrayLike");
    }

    private static JClass getGenericT(JCodeModel jCodeModel) {
        return jCodeModel.ref(GENERIC_TYPE_NAME);
    }

    private static JClass getJsArrayNarrowedClass(JCodeModel jCodeModel) {
        JClass jsArrayLikeClass = jCodeModel.ref(JsArrayLike.class);
        return  jsArrayLikeClass.narrow(getGenericT(jCodeModel));
    }


}
