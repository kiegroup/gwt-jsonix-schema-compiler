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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import javax.xml.namespace.QName;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JCommentPart;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTypeVar;
import com.sun.codemodel.JVar;
import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
import org.hisrc.jsonix.settings.LogLevelSetting;

import static gwt.jsonix.marshallers.xjc.plugin.utils.BuilderUtils.log;

public class JsUtilsBuilder {

    protected static final String GENERIC_TYPE_NAME = "D";
    protected static final String GENERIC_EXTEND_TYPE_NAME = "E";
    protected static final String ELEMENT = "element";
    protected static final int PUBLIC_STATIC_MODS = JMod.PUBLIC + JMod.STATIC;
    protected static final int PUBLIC_STATIC_NATIVE_MODS = PUBLIC_STATIC_MODS + JMod.NATIVE;
    protected static final int PRIVATE_STATIC_MODS = JMod.PRIVATE + JMod.STATIC;

    private static final String NEW_WRAPPED_INSTANCE_TEMPLATE = "\r\n     /**\n" +
            "     * Returns a <b>stub</b> object with <b>name</b> and <b>value</b> attributes\n" +
            "     * @return\n" +
            "     */\n" +
            "     public static native <D> D newWrappedInstance() /*-{\n" +
            "        var json = \"{\\\"name\\\": \\\"\\\", \\\"value\\\": \\\"\\\"}\";\n" +
            "        var retrieved = JSON.parse(json)\n" +
            "        return retrieved\n" +
            "    }-*/;\n";

    private static final String SET_NAME_ON_WRAPPED_TEMPLATE = "\r\n     /**\n" +
            "     * Set the <b>name</b> attribute of the given <b>wrapped</b> <code>D</code> with the <b>json</b> representation of the given <code>JSIName</code>\n" +
            "     * @param wrappedObject\n" +
            "     * @param name\n" +
            "     */\n" +
            "    public static native <D> void setNameOnWrapped(D wrappedObject, JSIName name) /*-{\n" +
            "        wrappedObject.name = name\n" +
            "    }-*/;";

    private static final String SET_VALUE_ON_WRAPPED_TEMPLATE = "\r\n     /**\n" +
            "     * Set the <b>value</b> attribute of the given <b>wrapped</b> <code>D</code> with the <b>json</b> representation of <b>value</b> <code>E</code>\n" +
            "     * @param wrappedObject\n" +
            "     * @param value\n" +
            "     */\n" +
            "    public static native <D, E> void setValueOnWrapped(D wrappedObject, E value) /*-{\n" +
            "        wrappedObject.value = value\n" +
            "    }-*/;";

    private static final String GET_UNWRAPPED_ELEMENTS_ARRAY_METHOD = "\r\n     /**\n" +
            "     * Returns a <code>JsArrayLike</code> where each element represents the <b>unwrapped</b> object (i.e. object.value) of the original one.\n" +
            "     * It the original <code>JsArrayLike</code> is <code>null</code>, returns a new, empty one\n" +
            "     * @param original\n" +
            "     * @param <D>\n" +
            "     * @return\n" +
            "     */\n" +
            "     public static native <D> JsArrayLike<D> getUnwrappedElementsArray(final JsArrayLike<D> original) /*-{\n" +
            "        var toReturn = [];\n" +
            "        if(original != null) {\n" +
            "            toReturn = original.map(function (arrayItem) {\n" +
            "                var retrieved = arrayItem.value\n" +
            "                var toSet = retrieved == null ? arrayItem : retrieved\n" +
            "                return toSet;\n" +
            "            });\n" +
            "        }\n" +
            "        return toReturn;\n" +
            "    }-*/;\n";

    private static final String GET_NATIVE_ELEMENTS_ARRAY_METHOD = "\r\n     /**\n" +
            "     * Returns the original <code>JsArrayLike</code> or, ift the original <code>JsArrayLike</code> is <code>null</code>, a new, empty one\n" +
            "     * @param original\n" +
            "     * @param <D>\n" +
            "     * @return\n" +
            "     */\n" +
            "     public static native <D> JsArrayLike<D> getNativeElementsArray(final JsArrayLike<D> original) /*-{\n" +
            "        if(original == null) {\n" +
            "            return [];\n" +
            "        } else {\n" +
            "            return original;\n" +
            "        }\n" +
            "    }-*/;";

    private static final String GET_UNWRAPPED_ELEMENT_METHOD = "\r\n     public static native Object getUnwrappedElement(final Object original) /*-{\n" +
            "        var toReturn = original.value;\n" +
            "        var toSet = toReturn == null ? original : toReturn;\n" +
            "        return toSet;\n" +
            "    }-*/;\n";

    private static final String GET_WRAPPED_ELEMENT_METHOD = "\r\n     public static native <D> D getWrappedElement(final Object value) /*-{\n" +
            "        var json = \"{\\\"name\\\": \\\"\\\", \\\"value\\\": \\\"\\\"}\";\n" +
            "        var toReturn = JSON.parse(json)\n" +
            "        toReturn.value =  value;\n" +
            "        return toReturn;\n" +
            "    }-*/;\n";

    private static final String GET_NATIVE_ARRAY_METHOD = "\r\n     /**\n" +
            "     * Helper method to create a new, empty <code>JsArrayLike</code>\n" +
            "     * @return\n" +
            "     */\n" +
            "     public static native <D> JsArrayLike<D> getNativeArray() /*-{\n" +
            "        return [];\n" +
            "    }-*/;\n";

    private static final String TO_ATTRIBUTES_MAP_METHOD = "\r\n     private static native void toAttributesMap(final Map<QName, String> toReturn,\n" +
            "                                               final Object original) /*-{\n" +
            "        var keys = Object.keys(original);\n" +
            "        for (var i = 0; i < keys.length; i++) {\n" +
            "            var key = keys[i];\n" +
            "            var value = original[key];\n" +
            "            @%1$s.JsUtils::putToAttributesMap(Ljava/util/Map;Ljava/lang/String;Ljava/lang/String;)(toReturn, key, value);\n" +
            "        }\n" +
            "    }-*/;\n";

    private static final String GET_EMPTY_JS_OBJECT_METHOD = "\r\n    /**\n" +
            "     * Helper method to create a new empty JavaScript object.\n" +
            "     * @return\n" +
            "     */\n" +
            "    private static native <D> D getJsObject() /*-{\n" +
            "        return {};\n" +
            "    }-*/;\n";

    private static final String PUT_TO_JS_OBJECT_METHOD = "\r\n    /**\n" +
            "     * Helper method to add a value to a JavaScript object at the associated key.\n" +
            "     */\n" + "" +
            "    private static native <D, K, V> void putToJavaScriptObject(final D jso, final K key, final V value) /*-{\n" +
            "        jso[key] = value;\n" +
            "    }-*/;\n";

    private static final String GET_TYPE_NAME = "\r\n    public static native String getTypeName(final Object instance) /*-{\n" +
            "        return instance.TYPE_NAME\n" +
            "    }-*/;\n";

    protected static final String GET_JSI_NAME_TEMPLATE = "\r\n    " +
            "public static native JSIName getJSIName(final String namespaceURI,\n" +
            "                                            final String localPart,\n" +
            "                                            final String prefix)/*-{\n" +
            "        var json = \"{\\\"namespaceURI\\\": \\\"\" + namespaceURI + \"\\\"," +
            " \\\"localPart\\\": \\\"\" + localPart + \"\\\"," +
            " \\\"prefix\\\": \\\"\" + prefix + \"\\\"," +
            " \\\"key\\\": \\\"{\" + namespaceURI + \"}\" + localPart + \"\\\"," +
            " \\\"string\\\": \\\"{\" + namespaceURI + \"}\" + prefix + \":\" + localPart + \"\\\"}\";\n" +
            "        return JSON.parse(json);\n" +
            "\r\n}-*/;";

    private JsUtilsBuilder() {
    }

    public static JDefinedClass generateJsUtilsClass(JCodeModel jCodeModel, String jsMainPackage) throws JClassAlreadyExistsException {
        final JDefinedClass toReturn = getJsUtilsClass(jCodeModel, jsMainPackage);
        populateJsUtilClass(jCodeModel, toReturn, jsMainPackage);
        return toReturn;
    }

    protected static void populateJsUtilClass(JCodeModel jCodeModel, JDefinedClass toPopulate, String jsMainPackage) {
        addEmptyConstructor(toPopulate);
        JMethod addMethod = addAddMethod(jCodeModel, toPopulate);
        addAddAllMethod(jCodeModel, toPopulate, addMethod);
        addRemoveMethod(jCodeModel, toPopulate);
        addToListMethod(jCodeModel, toPopulate);
        addToJsArrayLikeMethod(jCodeModel, toPopulate);
        addNewWrappedInstance(toPopulate);
        addSetNameOnWrappedObject(toPopulate);
        addSetValueOnWrappedObject(toPopulate);
        addGetNativeElementsArrayMethod(toPopulate);
        addGetUnwrappedElementsArrayMethod(toPopulate);
        addGetUnwrappedElementMethod(toPopulate);
        addGetWrappedElementMethod(toPopulate);
        addGetNativeArray(toPopulate);
        addJavaToAttributesMapMethod(jCodeModel, toPopulate);
        addNativeToAttributesMapMethod(toPopulate, jsMainPackage);
        addPutToAttributesMap(jCodeModel, toPopulate);
        addJavaFromAttributesMapMethod(jCodeModel, toPopulate);
        addNativeGetJsObjectMethod(toPopulate);
        addNativePutToJsObjectMethod(toPopulate);
        addGetTypeName(toPopulate);
        addGetJSIName(toPopulate);
    }

    protected static void addGetJSIName(final JDefinedClass jDefinedClass) {
        log(LogLevelSetting.DEBUG, "Add native 'getJSIName' method...");
        jDefinedClass.direct(GET_JSI_NAME_TEMPLATE);
    }

    protected static void addGetTypeName(final JDefinedClass jDefinedClass) {
        log(LogLevelSetting.DEBUG, "Add native 'getTypeName' method...");
        jDefinedClass.direct(GET_TYPE_NAME);
    }

    protected static JDefinedClass getJsUtilsClass(JCodeModel jCodeModel, String jsMainPackage) throws JClassAlreadyExistsException {
        log(LogLevelSetting.DEBUG, "Creating  JsUtils class");
        if (!jsMainPackage.isEmpty() && !jsMainPackage.endsWith(".")) {
            jsMainPackage += ".";
        }
        final JDefinedClass toReturn = jCodeModel._class(jsMainPackage + "JsUtils");
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
        JClass genericE = getGenericTExtends(jCodeModel);
        final JMethod toReturn = getGenerifiedJMethod(jsUtils, Void.TYPE, "addAll");
        toReturn.generify(GENERIC_EXTEND_TYPE_NAME, genericT);
        final JVar jsArrayLikeParameter = getJSArrayNarrowedJVar(jCodeModel, toReturn);
        final JVar elementParam = toReturn.varParam(genericE, "elements");
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

    protected static JMethod addToJsArrayLikeMethod(JCodeModel jCodeModel, JDefinedClass jsUtils) {
        log(LogLevelSetting.DEBUG, "Add 'toJsArrayLike' method...");
        final JClass genericT = getGenericT(jCodeModel);
        JClass narrowedJsArrayLike = getJsArrayNarrowedClass(jCodeModel);
        JClass rawArrayListClass = jCodeModel.ref(List.class);
        JClass listField = rawArrayListClass.narrow(genericT);

        final JMethod toReturn = getGenerifiedJMethod(jsUtils, narrowedJsArrayLike, "toJsArrayLike");
        final JVar listParameter = toReturn.param(JMod.FINAL, listField, "list");
        final JBlock block = toReturn.body();

        final JVar listToReturn = block.decl(JMod.FINAL, narrowedJsArrayLike, "toReturn", JExpr.invoke("getNativeArray"));
        final JConditional nonNull = block._if(jCodeModel.ref(Objects.class).staticInvoke("nonNull").arg(listParameter));
        final JForLoop jForLoop = nonNull._then()._for();
        final JVar i = jForLoop.init(jCodeModel.INT, "i", JExpr.lit(0));
        jForLoop.test(i.lt(listParameter.invoke("size")));
        jForLoop.update(i.incr());
        final JBlock forLoopBody = jForLoop.body();
        final JVar toAdd = forLoopBody.decl(JMod.FINAL, genericT, "toAdd", jCodeModel.ref(Js.class).staticInvoke("uncheckedCast").arg(listParameter.invoke("get").arg(i)));
        forLoopBody.add(listToReturn.invoke("setAt").arg(listToReturn.invoke("getLength")).arg(toAdd));
        block._return(listToReturn);
        return toReturn;
    }

    protected static void addNewWrappedInstance(JDefinedClass jDefinedClass) {
        log(LogLevelSetting.DEBUG, "Add 'newWrappedInstance' method...");
        String directString = String.format(NEW_WRAPPED_INSTANCE_TEMPLATE, jDefinedClass.name());
        jDefinedClass.direct(directString);
    }

    protected static void addSetNameOnWrappedObject(JDefinedClass jDefinedClass) {
        log(LogLevelSetting.DEBUG, "Add 'setNameOnWrapped' method...");
        String directString = String.format(SET_NAME_ON_WRAPPED_TEMPLATE, jDefinedClass.name());
        jDefinedClass.direct(directString);
    }

    protected static void addSetValueOnWrappedObject(JDefinedClass jDefinedClass) {
        log(LogLevelSetting.DEBUG, "Add 'setValueOnWrapped' method...");
        String directString = String.format(SET_VALUE_ON_WRAPPED_TEMPLATE, jDefinedClass.name());
        jDefinedClass.direct(directString);
    }

    protected static void addGetUnwrappedElementsArrayMethod(JDefinedClass jsUtils) {
        log(LogLevelSetting.DEBUG, "Add 'getUnwrappedElementsArray' method...");
        jsUtils.direct(GET_UNWRAPPED_ELEMENTS_ARRAY_METHOD);
    }

    protected static void addGetNativeElementsArrayMethod(JDefinedClass jsUtils) {
        log(LogLevelSetting.DEBUG, "Add 'getNativeElementsArray' method...");
        jsUtils.direct(GET_NATIVE_ELEMENTS_ARRAY_METHOD);
    }

    protected static void addGetUnwrappedElementMethod(JDefinedClass jsUtils) {
        log(LogLevelSetting.DEBUG, "Add 'getUnwrappedElement' method...");
        jsUtils.direct(GET_UNWRAPPED_ELEMENT_METHOD);
    }

    protected static void addGetWrappedElementMethod(JDefinedClass jsUtils) {
        log(LogLevelSetting.DEBUG, "Add 'getWrappedElement' method...");
        jsUtils.direct(GET_WRAPPED_ELEMENT_METHOD);
    }

    protected static void addGetNativeArray(JDefinedClass jsUtils) {
        log(LogLevelSetting.DEBUG, "Add 'getNativeArray' method...");
        jsUtils.direct(GET_NATIVE_ARRAY_METHOD);
    }

    protected static void addNativeGetJsObjectMethod(JDefinedClass jsUtils) {
        log(LogLevelSetting.DEBUG, "Add native 'getJsObject' method...");
        jsUtils.direct(GET_EMPTY_JS_OBJECT_METHOD);
    }

    protected static void addNativePutToJsObjectMethod(JDefinedClass jsUtils) {
        log(LogLevelSetting.DEBUG, "Add native 'putToJsObject' method...");
        jsUtils.direct(PUT_TO_JS_OBJECT_METHOD);
    }

    /**
     * @param jCodeModel
     * @param jsUtils
     * @return
     */
    protected static JMethod addJavaToAttributesMapMethod(JCodeModel jCodeModel, JDefinedClass jsUtils) {
        log(LogLevelSetting.DEBUG, "Add java 'toAttributesMap' method...");
        final JClass qName = jCodeModel.ref(QName.class);
        JClass narrowedMap = getQNameStringNarrowedMapClass(jCodeModel);
        JClass rawHashMapClass = jCodeModel.ref(HashMap.class);
        JClass hashMapField = rawHashMapClass.narrow(qName, jCodeModel.ref(String.class));
        final JMethod toReturn = getJMethod(jsUtils, narrowedMap, "toAttributesMap");
        final JVar originalParameter = toReturn.param(JMod.FINAL, jCodeModel.ref(Object.class), "original");
        final JBlock block = toReturn.body();
        final JVar mapToReturn = block.decl(JMod.FINAL, narrowedMap, "toReturn", JExpr._new(hashMapField));
        final JConditional nonNull = block._if(jCodeModel.ref(Objects.class).staticInvoke("nonNull").arg(originalParameter));
        nonNull._then().invoke("toAttributesMap").arg(mapToReturn).arg(originalParameter);
        block._return(mapToReturn);
        final JDocComment javadoc = toReturn.javadoc();
        String commentString = "Extracts the <b>otherAttributes</b> property from a JavaScriptObject to a <i>regular</i> Java Map.";
        javadoc.append(commentString);
        JCommentPart setterPart = javadoc.addParam("original");
        commentString = " <b>js object</b> to transform.";
        setterPart.add(commentString);
        JCommentPart returnPart = javadoc.addReturn();
        commentString = "the populated <code>Map&lt;QName, String&gt;</code>";
        returnPart.add(commentString);
        return toReturn;
    }

    protected static JMethod addPutToAttributesMap(JCodeModel jCodeModel, JDefinedClass jsUtils) {
        log(LogLevelSetting.DEBUG, "Add 'putToAttributesMap' method...");
        final JClass qName = jCodeModel.ref(QName.class);
        JClass narrowedMap = getQNameStringNarrowedMapClass(jCodeModel);
        final JMethod toReturn = jsUtils.method(PRIVATE_STATIC_MODS, Void.TYPE, "putToAttributesMap");
        final JVar mapParameter = toReturn.param(JMod.FINAL, narrowedMap, "destination");
        final JVar qNameAsStringParameter = toReturn.param(JMod.FINAL, jCodeModel.ref(String.class), "qNameAsString");
        final JVar valueParameter = toReturn.param(JMod.FINAL, jCodeModel.ref(String.class), "value");
        final JBlock block = toReturn.body();
        block.add(mapParameter.invoke("put").arg(qName.staticInvoke("valueOf").arg(qNameAsStringParameter)).arg(valueParameter));
        final JDocComment javadoc = toReturn.javadoc();
        String commentString = "Create a <code>QName</code> instance from the given <b>qNameAsString</b>, and the use it as key for a new entry on <b>destination</b> Map.";
        javadoc.append(commentString);
        JCommentPart setterPart = javadoc.addParam("destination");
        commentString = " the <code>Map</code> to populate.";
        setterPart.add(commentString);
        setterPart = javadoc.addParam("qNameAsString");
        commentString = " the <code>String</code> to transform to <code>QName</code> instance used as key.";
        setterPart.add(commentString);
        setterPart = javadoc.addParam("value");
        commentString = " the <b>value</b> to be used in the new entry.";
        setterPart.add(commentString);
        return toReturn;
    }

    protected static void addNativeToAttributesMapMethod(JDefinedClass jsUtils, String jsMainPackage) {
        log(LogLevelSetting.DEBUG, "Add native 'toAttributesMap' method...");
        jsUtils.direct(String.format(TO_ATTRIBUTES_MAP_METHOD, jsMainPackage));
    }

    /**
     * @param jCodeModel
     * @param jsUtils
     * @return
     */
    protected static JMethod addJavaFromAttributesMapMethod(JCodeModel jCodeModel, JDefinedClass jsUtils) {
        log(LogLevelSetting.DEBUG, "Add java 'fromAttributesMapMethod' method...");
        final JClass narrowedMap = getQNameStringNarrowedMapClass(jCodeModel);
        final JMethod toReturn = getJMethod(jsUtils, jCodeModel.ref(Object.class), "fromAttributesMap");
        final JTypeVar type = toReturn.generify("D");
        toReturn.type(type);
        final JVar originalParam = toReturn.param(JMod.FINAL, narrowedMap, "original");
        final JBlock block = toReturn.body();
        final JVar mapToReturn = block.decl(JMod.FINAL, type, "toReturn", JExpr.invoke("getJsObject"));

        final JClass narrowedConsumer = getQNameStringNarrowedMapEntryConsumerClass(jCodeModel);
        JDefinedClass anonymousNarrowedConsumerProducer = jCodeModel.anonymousClass(narrowedConsumer);
        final JMethod acceptMethod = anonymousNarrowedConsumerProducer.method(JMod.PUBLIC, jCodeModel.VOID, "accept");
        acceptMethod.annotate(Override.class);
        final JVar entry = acceptMethod.param(getQNameStringNarrowedMapEntryClass(jCodeModel), "entry");
        JBlock methodBody = acceptMethod.body();
        methodBody.invoke("putToJavaScriptObject")
                .arg(mapToReturn)
                .arg(entry.invoke("getKey").invoke("toString"))
                .arg(entry.invoke("getValue"));
        block.add((originalParam.invoke("entrySet").invoke("stream").invoke("forEach")).arg(JExpr._new(anonymousNarrowedConsumerProducer)));
        block._return(mapToReturn);
        final JDocComment javadoc = toReturn.javadoc();
        String commentString = "Extracts the <b>otherAttributes</b> property from a <i>regular<i> Java Map to a JavaScriptObject.";
        javadoc.append(commentString);
        JCommentPart setterPart = javadoc.addParam("original");
        commentString = " the <code>Map&lt;QName, String&gt;</code> to transform.";
        setterPart.add(commentString);
        JCommentPart returnPart = javadoc.addReturn();
        commentString = "the populated JavaScriptObject";
        returnPart.add(commentString);
        return toReturn;
    }

    protected static JMethod getGenerifiedJMethod(JDefinedClass jsUtils, Class<?> returnType, String methodName) {
        JMethod toReturn = getJMethod(jsUtils, returnType, methodName);
        toReturn.generify(GENERIC_TYPE_NAME);
        return toReturn;
    }

    protected static JMethod getGenerifiedJMethod(JDefinedClass jsUtils, JClass returnType, String methodName) {
        JMethod toReturn = getJMethod(jsUtils, returnType, methodName);
        toReturn.generify(GENERIC_TYPE_NAME);
        return toReturn;
    }

    // TODO {gcardosi} delete when we are sure it is unneeded
//    protected static JMethod getGenerifiedNativeJMethod(JDefinedClass jsUtils, Class<?> returnType, String methodName) {
//        JMethod toReturn = getNativeJMethod(jsUtils, returnType, methodName);
//        toReturn.generify(GENERIC_TYPE_NAME);
//        return toReturn;
//    }

    // TODO {gcardosi} delete when we are sure it is unneeded
//    protected static JMethod getGenerifiedNativeJMethod(JDefinedClass jsUtils, JClass returnType, String methodName) {
//        JMethod toReturn = getNativeJMethod(jsUtils, returnType, methodName);
//        toReturn.generify(GENERIC_TYPE_NAME);
//        return toReturn;
//    }

    protected static JMethod getJMethod(JDefinedClass jsUtils, Class<?> returnType, String methodName) {
        return jsUtils.method(PUBLIC_STATIC_MODS, returnType, methodName);
    }

    protected static JMethod getJMethod(JDefinedClass jsUtils, JClass returnType, String methodName) {
        return jsUtils.method(PUBLIC_STATIC_MODS, returnType, methodName);
    }

    protected static JMethod getNativeJMethod(JDefinedClass jsUtils, Class<?> returnType, String methodName) {
        return jsUtils.method(PUBLIC_STATIC_NATIVE_MODS, returnType, methodName);
    }

    protected static JMethod getNativeJMethod(JDefinedClass jsUtils, JClass returnType, String methodName) {
        return jsUtils.method(PUBLIC_STATIC_NATIVE_MODS, returnType, methodName);
    }

    protected static JVar getJSArrayNarrowedJVar(JCodeModel jCodeModel, JMethod jmethod) {
        return jmethod.param(JMod.FINAL, getJsArrayNarrowedClass(jCodeModel), "jsArrayLike");
    }

    protected static JClass getGenericT(JCodeModel jCodeModel) {
        return jCodeModel.ref(GENERIC_TYPE_NAME);
    }

    protected static JClass getGenericTExtends(JCodeModel jCodeModel) {
        return jCodeModel.ref(GENERIC_EXTEND_TYPE_NAME);
    }

    protected static JClass getJsArrayNarrowedClass(JCodeModel jCodeModel) {
        JClass jsArrayLikeClass = jCodeModel.ref(JsArrayLike.class);
        return jsArrayLikeClass.narrow(getGenericT(jCodeModel));
    }

    protected static JClass getQNameStringNarrowedMapClass(JCodeModel jCodeModel) {
        JClass rawMapClass = jCodeModel.ref(Map.class);
        final JClass qName = jCodeModel.ref(QName.class);
        return rawMapClass.narrow(qName, jCodeModel.ref(String.class));
    }

    protected static JClass getQNameStringNarrowedMapEntryClass(JCodeModel jCodeModel) {
        JClass rawMapClass = jCodeModel.directClass(Map.Entry.class.getCanonicalName());
        return rawMapClass.narrow(QName.class, String.class);
    }

    protected static JClass getQNameStringNarrowedMapEntryConsumerClass(JCodeModel jCodeModel) {
        final JClass narrowedQnameMapEntryClass = getQNameStringNarrowedMapEntryClass(jCodeModel);
        final JClass rawConsumer = jCodeModel.ref(Consumer.class);
        return rawConsumer.narrow(narrowedQnameMapEntryClass);
    }
}
