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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JEnumConstant;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.model.CElement;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CEnumLeafInfo;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.model.nav.NClass;
import gwt.jsonix.marshallers.xjc.plugin.exceptions.ParseModelException;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsArrayLike;
import org.hisrc.jsonix.settings.LogLevelSetting;
import org.jvnet.jaxb2_commons.plugin.inheritance.Customizations;
import org.jvnet.jaxb2_commons.plugin.inheritance.ExtendsClass;
import org.jvnet.jaxb2_commons.plugin.inheritance.ExtendsClassReader;
import org.jvnet.jaxb2_commons.plugin.inheritance.util.JavaTypeParser;
import org.jvnet.jaxb2_commons.util.CustomizationUtils;

import static gwt.jsonix.marshallers.xjc.plugin.builders.BuilderUtils.getJavaRef;
import static gwt.jsonix.marshallers.xjc.plugin.builders.BuilderUtils.log;
import static org.jvnet.jaxb2_commons.plugin.inheritance.Customizations.EXTENDS_ELEMENT_NAME;

/**
 * Actual builder for <b>JSInterop</b> models
 */
public class ModelBuilder {

    protected static final String NEW_INSTANCE_TEMPLATE = "\r\n    public static native %1$s newInstance() /*-{\n" +
            "        var json = \"{\\\"TYPE_NAME\\\": \\\"%2$s\\\"}\";\n" +
            "        var retrieved = JSON.parse(json)\n" +
            "        return retrieved\n" +
            "    }-*/;\n";

    protected static final String INSTANCE_OF_TEMPLATE = "\r\n    public static native boolean instanceOf(Object instance) /*-{\n" +
            "       return instance.TYPE_NAME != null && instance.TYPE_NAME === \"%1$s\"\n" +
            "    }-*/;\n";

    protected static final String GET_JSARRAY_TEMPLATE = "\r\n    /**\n" +
            "     * Returns a <code>%1$s</code> where each element represents the <b>unwrapped</b> object (i.e. object.value) of the original one\n" +
            "     * @param instance\n" +
            "     * @return\n" +
            "     */\n" +
            "    public static native %1$s get%2$s(%3$s instance) /*-{\n" +
            "        instance.%5$s = @org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils::getNativeElementsArray(Ljsinterop/base/JsArrayLike;)(instance.%5$s)\n" +
            "        return @%4$s.JsUtils::getUnwrappedElementsArray(Ljsinterop/base/JsArrayLike;)(instance.%5$s)\n" +
            "    }-*/;\n";

    protected static final String GET_NATIVE_JSARRAY_TEMPLATE = "\r\n    /**\n" +
            "     * Returns a <code>%1$s</code> where each element represents the original <b>wrapped</b> object (i.e. the whole object)\n" +
            "     * @param instance\n" +
            "     * @return\n" +
            "     */\n" +
            "    public static native %1$s getNative%2$s(%3$s instance) /*-{\n" +
            "        instance.%5$s = @org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils::getNativeElementsArray(Ljsinterop/base/JsArrayLike;)(instance.%5$s)\n" +
            "        return instance.%5$s\n" +
            "    }-*/;\n";

    protected static final String ADD_TO_JSARRAY_TEMPLATE = "\r\n    /**\n" +
            "     * Add a <b>wrapped</b> representation of <code>%3$s</code> to <code>%2$s.%5$s</code> \n" +
            "     * @param instance \n" +
            "     * @param toAdd the <b>wrapped</b> <code>%3$s</code> to add\n" +
            "     */\n" +
            "    public static native void add%1$s(%2$s instance, %3$s toAdd) /*-{\n" +
            "        instance.%5$s = @%6$s::getNative%1$s(%7$s;)(instance)\n" +
            "        return @%4$s.JsUtils::add(Ljsinterop/base/JsArrayLike;Ljava/lang/Object;)(instance.%5$s, toAdd)\n" +
            "    }-*/;\n";

    protected static final String ADDALL_TO_JSARRAY_TEMPLATE = "\r\n    /**\n" +
            "     * Add the <b>wrapped</b> representations of all given <code>%3$s</code>s to <code>%2$s.%5$s</code>\n" +
            "     * @param instance \n" +
            "     * @param toAdd <code>JsArrayLike</code> of <b>wrapped</b> <code>%3$s</code>s to add\n" +
            "     */\n" +
            "    public static native void addAll%1$s(%2$s instance, JsArrayLike<? extends %3$s> toAdd) /*-{\n" +
            "        instance.%5$s = @%6$s::getNative%1$s(%7$s;)(instance)\n" +
            "        return @%4$s.JsUtils::addAll(Ljsinterop/base/JsArrayLike;[Ljava/lang/Object;)(instance.%5$s, toAdd)\n" +
            "    }-*/;\n";

    protected static final String REMOVE_JSARRAY_TEMPLATE = "\r\n    public static native void remove%1$s(%2$s instance, int index) /*-{\n" +
            "        instance.%4$s = @%5$s::getNative%1$s(%6$s;)(instance)\n" +
            "        return @%3$s.JsUtils::remove(Ljsinterop/base/JsArrayLike;I)(instance.%4$s, index)\n" +
            "    }-*/;\n";

    protected static final String GET_OTHER_ATTRIBUTES_TEMPLATE = "\r\n    public static native Map<QName, String> getOtherAttributesMap(final %1$s instance) /*-{\n" +
            "        return @%2$s.JsUtils::toAttributesMap(Ljava/lang/Object;)(instance.otherAttributes)\n" +
            "    }-*/;\n";

    protected static final String SET_OTHER_ATTRIBUTES_TEMPLATE = "\r\n    public static native void setOtherAttributesMap(final %1$s instance, final Map<QName, String> attributes) /*-{\n" +
            "        var otherAttributes = @%2$s.JsUtils::fromAttributesMap(Ljava/util/Map;)(attributes);\n" +
            "        instance.otherAttributes = otherAttributes;\n" +
            "    }-*/;\n";

    protected static final String GET_JSINAME_TEMPLATE = "\r\n    public static native %1$s getJSIName()/*-{\n" +
            "        var json = \"{\\\"namespaceURI\\\": \\\"%2$s\\\"," +
            " \\\"localPart\\\": \\\"%3$s\\\"," +
            " \\\"prefix\\\": \\\"%4$s\\\"," +
            " \\\"key\\\": \\\"{%2$s}%3$s\\\"," +
            " \\\"string\\\": \\\"{%2$s}%4$s:%3$s\\\"}\";\n" +
            "        var toReturn = JSON.parse(json)\n" +
            "        return toReturn\n" +
            "}-*/;";

    private ModelBuilder() {
    }

    /**
     * Method to create the <b>JSInterop</b> representation oif <b>xsd</b> definitions
     * @param definedClassesMap
     * @param model
     * @param jCodeModel
     * @param packageModuleMap
     * @param jsUtilsClass
     * @throws Exception
     */
    public static void generateJSInteropModels(Map<String, JClass> definedClassesMap, Model model, JCodeModel jCodeModel, Map<String, String> packageModuleMap, JDefinedClass jsUtilsClass, JDefinedClass jsiNameClass) throws ParseModelException, JClassAlreadyExistsException {
        definedClassesMap.clear();
        log(LogLevelSetting.DEBUG, "Generating JSInterop code...");
        for (CClassInfo cClassInfo : model.beans().values()) {
            populateJCodeModel(definedClassesMap, jCodeModel, cClassInfo, packageModuleMap, model, jsUtilsClass, jsiNameClass);
        }
    }

    protected static void populateJCodeModel(Map<String, JClass> definedClassesMap, JCodeModel toPopulate, CClassInfo cClassInfo, Map<String, String> packageModuleMap, Model model, JDefinedClass jsUtilsClass, JDefinedClass jsiNameClass) throws JClassAlreadyExistsException, ParseModelException {
        log(LogLevelSetting.DEBUG, "Generating  JCode model...");
        if (definedClassesMap.containsKey(cClassInfo.fullName())) {
            return;
        }
        final CClassInfoParent parent = cClassInfo.parent();
        final JDefinedClass jDefinedClass;
        final JExpression nameSpaceExpression;
        final CClassInfo basecClassInfo = cClassInfo.getBaseClass();
        JClass jDefinedBaseClass = null;

        String shortClassName = cClassInfo.shortName;
        String nameSpace = shortClassName;

        // Read extends customisation from JAXB Basics Inheritance plugin binding.
        // Explicit values found by JAXB bindings are overwritten by inheritance defined in the XSD being processed.
        final CPluginCustomization extendsClassCustomization = CustomizationUtils.findCustomization(cClassInfo, EXTENDS_ELEMENT_NAME);
        if (Objects.nonNull(extendsClassCustomization)) {
            jDefinedBaseClass = getFromExtendsClassCustomization(definedClassesMap, toPopulate, extendsClassCustomization);
        }
        if (basecClassInfo != null) { // This is the "extended" class
            jDefinedBaseClass = getFromBasecClassInfo(definedClassesMap, toPopulate, packageModuleMap, model, basecClassInfo, jsUtilsClass, jsiNameClass);
        }
        boolean hasClassParent = (parent != null && !(parent instanceof CClassInfoParent.Package));
        String parentNamespace = null;
        if (hasClassParent && definedClassesMap.containsKey(parent.fullName())) { // This is for inner classes
            String parentFullName = parent.fullName();
            jDefinedClass = getFromParent(parent, jDefinedBaseClass, definedClassesMap, nameSpace);
            parentNamespace = parentFullName.contains(".") ? parentFullName.substring(parentFullName.lastIndexOf('.') + 1) : parentFullName;
            nameSpaceExpression = JExpr.lit(parentNamespace);
            nameSpace = parentNamespace + "." + nameSpace;
        } else {
            String fullClassName = cClassInfo.getOwnerPackage().name() + ".JSI" + nameSpace;
            jDefinedClass = jDefinedBaseClass != null ? toPopulate._class(fullClassName)._extends(jDefinedBaseClass) : toPopulate._class(fullClassName);
            nameSpaceExpression = toPopulate.ref(JsPackage.class).staticRef("GLOBAL");
        }
        definedClassesMap.put(cClassInfo.fullName(), jDefinedClass);
        JDocComment comment = jDefinedClass.javadoc();
        String commentString = "JSInterop adapter for <code>" + nameSpace + "</code>";
        comment.append(commentString);
        String jsTypeName = "JsInterop__ConstructorAPI__DMN__JSI" + shortClassName;

        jDefinedClass.annotate(toPopulate.ref(JsType.class))
                .param("namespace", nameSpaceExpression)
                .param("name", jsTypeName)
                .param("isNative", true);

        String moduleName = packageModuleMap.get(jDefinedClass._package().name());
        addInstanceOf(jDefinedClass, jsUtilsClass, moduleName, nameSpace);
        addTypeName(jDefinedClass, toPopulate, moduleName, nameSpace);
        //if (basecClassInfo == null) {
            addGetTypeNameProperty(toPopulate, jDefinedClass, nameSpace);
        //}
        for (CPropertyInfo cPropertyInfo : cClassInfo.getProperties()) {
            addProperty(toPopulate, jDefinedClass, cPropertyInfo, definedClassesMap, packageModuleMap, model, nameSpace, jsUtilsClass, jsiNameClass);
        }
        if (cClassInfo.declaresAttributeWildcard()) {
            addOtherAttributesProperty(toPopulate, jDefinedClass, jsUtilsClass, nameSpace);
        }
    }

    protected static void populateJCodeModel(Map<String, JClass> definedClassesMap, JCodeModel toPopulate, CEnumLeafInfo cEnumLeafInfo) throws JClassAlreadyExistsException {
        log(LogLevelSetting.DEBUG, "Generating  JCode model...");
        String fullClassName = cEnumLeafInfo.parent.getOwnerPackage().name() + ".JSI" + cEnumLeafInfo.shortName;
        final JDefinedClass jDefinedClass = toPopulate._class(fullClassName, ClassType.ENUM);
        jDefinedClass.annotate(toPopulate.ref(JsType.class))
                .param("name", cEnumLeafInfo.shortName);
        definedClassesMap.put(cEnumLeafInfo.fullName(), jDefinedClass);
        JDocComment comment = jDefinedClass.javadoc();
        String commentString = "JSInterop adapter for <code>" + cEnumLeafInfo.shortName + "</code>";
        comment.append(commentString);
        cEnumLeafInfo.getConstants().forEach(cEnumConstant -> {
            final JEnumConstant jEnumConstant = jDefinedClass.enumConstant(cEnumConstant.getName());
            if (cEnumLeafInfo.needsValueField()) {
                jEnumConstant.arg(JExpr.lit(cEnumConstant.getLexicalValue()));
            }
        });
        if (cEnumLeafInfo.needsValueField()) {
            addEnumValueField(toPopulate, jDefinedClass);
        }
    }

    protected static JClass getFromExtendsClassCustomization(Map<String, JClass> definedClassesMap, JCodeModel toPopulate, CPluginCustomization extendsClassCustomization) {
        final ExtendsClass extendsClass = (ExtendsClass) CustomizationUtils.unmarshall(Customizations.getContext(), extendsClassCustomization);
        final String extendsClassName = ExtendsClassReader.getValue(extendsClass);
        return parseClass(extendsClassName, toPopulate, definedClassesMap);
    }

    protected static JClass getFromBasecClassInfo(Map<String, JClass> definedClassesMap, JCodeModel toPopulate, Map<String, String> packageModuleMap, Model model, CClassInfo basecClassInfo, JDefinedClass jsUtilsClass, JDefinedClass jsiNameClass) throws ParseModelException, JClassAlreadyExistsException {
        if (!definedClassesMap.containsKey(basecClassInfo.fullName())) {
            populateJCodeModel(definedClassesMap, toPopulate, basecClassInfo, packageModuleMap, model, jsUtilsClass, jsiNameClass);
        }
        return definedClassesMap.get(basecClassInfo.fullName());
    }

    protected static JDefinedClass getFromParent(CClassInfoParent parent, JClass jDefinedBaseClass, Map<String, JClass> definedClassesMap, String nameSpace) throws JClassAlreadyExistsException {
        int mod = JMod.PUBLIC + JMod.STATIC;
        String parentFullName = parent.fullName();
        return jDefinedBaseClass != null ? ((JDefinedClass) definedClassesMap.get(parentFullName))._class(mod, "JSI" + nameSpace)._extends(jDefinedBaseClass) : ((JDefinedClass) definedClassesMap.get(parentFullName))._class(mod, "JSI" + nameSpace);
    }

    protected static void addEnumValueField(JCodeModel toPopulate, JDefinedClass jDefinedClass) {
        final JClass propertyRef = toPopulate.ref(String.class);
        String privatePropertyName = "value";
        int mod = JMod.PRIVATE + JMod.FINAL;
        final JFieldVar field = jDefinedClass.field(mod, propertyRef, privatePropertyName);
        mod = JMod.NONE;
        final JMethod constructor = jDefinedClass.constructor(mod);
        final JVar param = constructor.param(propertyRef, privatePropertyName);
        constructor.body().assign(JExpr._this().ref(field), param);
        mod = JMod.PUBLIC;
        JMethod getterMethod = jDefinedClass.method(mod, propertyRef, privatePropertyName);
        getterMethod.body()._return(field);
    }

    protected static void addInstanceOf(final JDefinedClass jDefinedClass,
                                        final JDefinedClass jsUtilsClass,
                                        final String moduleName,
                                        final String originalName) {

        final String fullTypeName = moduleName + "." + originalName;
        final int mods = JMod.PUBLIC + JMod.STATIC;
        final String methodName = "instanceOf" + jDefinedClass.name();

        final JMethod instanceOfMethod = jDefinedClass.method(mods, Boolean.class, methodName);
        final JBlock block = instanceOfMethod.body();
        final JVar typeParam = instanceOfMethod.param(JMod.FINAL, Object.class, "instance");
        final JInvocation getTypeName = jsUtilsClass.staticInvoke("getTypeName").arg(typeParam);

        instanceOfMethod.annotate(JsOverlay.class);

        block._return(JExpr.lit(fullTypeName).invoke("equals").arg(getTypeName));
    }

    protected static void addTypeName(JDefinedClass jDefinedClass, JCodeModel jCodeModel, String moduleName, String originalName) {

        final JClass propertyRef = jCodeModel.ref(String.class);
        final String fullName = moduleName + "." + originalName;
        final int mods = JMod.PUBLIC + JMod.STATIC + JMod.FINAL;
        final JFieldVar typeNameField = jDefinedClass.field(mods, propertyRef, "TYPE");

        typeNameField.annotate(JsOverlay.class);
        typeNameField.init(JExpr.lit(fullName));
    }

    protected static void addStaticJsArrayGetter(JDefinedClass jDefinedClass, String jsArrayType, String specificGetNamePart, String propertyName, String packageName) {
        log(LogLevelSetting.DEBUG, String.format("Add get%1$s method to object %2$s.%3$s ...", specificGetNamePart, jDefinedClass._package().name(), jDefinedClass.name()));
        String directString = String.format(GET_JSARRAY_TEMPLATE, jsArrayType, specificGetNamePart, jDefinedClass.name(), packageName, propertyName);
        jDefinedClass.direct(directString);
    }

    protected static void addStaticNativeJsArrayGetter(JDefinedClass jDefinedClass, String jsArrayType, String specificGetNamePart, String propertyName, String packageName) {
        log(LogLevelSetting.DEBUG, String.format("Add getNative%1$s method to object %2$s.%3$s ...", specificGetNamePart, jDefinedClass._package().name(), jDefinedClass.name()));
        String directString = String.format(GET_NATIVE_JSARRAY_TEMPLATE, jsArrayType, specificGetNamePart, jDefinedClass.name(), packageName, propertyName);
        jDefinedClass.direct(directString);
    }

    protected static void addStaticJsArrayAdd(JDefinedClass jDefinedClass, String toAddType, String specificGetNamePart, String propertyName, String packageName) {
        log(LogLevelSetting.DEBUG, String.format("Add add%1$s method to object %2$s.%3$s ...", specificGetNamePart, jDefinedClass._package().name(), jDefinedClass.name()));
        String fullName = jDefinedClass.fullName();
        String jsniName = getJNIRepresentation(jDefinedClass);
        String directString = String.format(ADD_TO_JSARRAY_TEMPLATE, specificGetNamePart, jDefinedClass.name(), toAddType, packageName, propertyName, fullName, jsniName);
        jDefinedClass.direct(directString);
    }

    protected static void addStaticJsArrayAddAll(JDefinedClass jDefinedClass, String toAddType, String specificGetNamePart, String propertyName, String packageName) {
        log(LogLevelSetting.DEBUG, String.format("Add addAll%1$s method to object %2$s.%3$s ...", specificGetNamePart, jDefinedClass._package().name(), jDefinedClass.name()));
        String fullName = jDefinedClass.fullName();
        String jsniName = getJNIRepresentation(jDefinedClass);
        String directString = String.format(ADDALL_TO_JSARRAY_TEMPLATE, specificGetNamePart, jDefinedClass.name(), toAddType, packageName, propertyName, fullName, jsniName);
        jDefinedClass.direct(directString);
    }

    protected static void addStaticJsArrayRemove(JDefinedClass jDefinedClass, String specificGetNamePart, String propertyName, String packageName) {
        log(LogLevelSetting.DEBUG, String.format("Add remove%1$s method to object %2$s.%3$s ...", specificGetNamePart, jDefinedClass._package().name(), jDefinedClass.name()));
        String fullName = jDefinedClass.fullName();
        String jsniName = getJNIRepresentation(jDefinedClass);
        String directString = String.format(REMOVE_JSARRAY_TEMPLATE, specificGetNamePart, jDefinedClass.name(), packageName, propertyName, fullName, jsniName);
        jDefinedClass.direct(directString);
    }

    protected static void addGetTypeNameProperty(JCodeModel jCodeModel, JDefinedClass jDefinedClass, String namespace) {
        log(LogLevelSetting.DEBUG, String.format("Add getTYPENAME property to object %1$s.%2$s ...", jDefinedClass._package().name(), jDefinedClass.name()));
        JClass parameterRef = jCodeModel.ref(String.class);
        addNativeGetter(jCodeModel, jDefinedClass, parameterRef, "TYPE_NAME", "TYPE_NAME");
    }

    protected static void addProperty(JCodeModel jCodeModel, JDefinedClass jDefinedClass, CPropertyInfo cPropertyInfo, Map<String, JClass> definedClassesMap, Map<String, String> packageModuleMap, Model model, String nameSpace, JDefinedClass jsUtilsClass, JDefinedClass jsiNameClass) throws ParseModelException, JClassAlreadyExistsException {

        final JClass propertyRef = getPropertyRef(jCodeModel, cPropertyInfo, jDefinedClass.fullName(), definedClassesMap, packageModuleMap, model, jsUtilsClass);
        final String publicPropertyName = cPropertyInfo.getName(true);
        final String privatePropertyName = cPropertyInfo.getName(false);

        addGetter(jCodeModel, jDefinedClass, jsUtilsClass, nameSpace, propertyRef, publicPropertyName, privatePropertyName);
        addSetter(jCodeModel, jDefinedClass, propertyRef, publicPropertyName, privatePropertyName, nameSpace);
        if (cPropertyInfo.isCollection() && propertyRef.getTypeParameters() != null && !propertyRef.getTypeParameters().isEmpty()) {
            final JType typeParameter = propertyRef.getTypeParameters().get(0).unboxify();
            String propertyType = typeParameter.name();
            addStaticJsArrayGetter(jDefinedClass, propertyRef.name(), publicPropertyName, privatePropertyName, packageName);
            addStaticNativeJsArrayGetter(jDefinedClass, propertyRef.name(), publicPropertyName, privatePropertyName, packageName);
            addStaticJsArrayAdd(jDefinedClass, propertyType, publicPropertyName, privatePropertyName, packageName);
            if (!typeParameter.isPrimitive()) {
                addStaticJsArrayAddAll(jDefinedClass, propertyType, publicPropertyName, privatePropertyName, packageName);
            }
            addStaticJsArrayRemove(jDefinedClass, publicPropertyName, privatePropertyName, packageName);
        }
    }

    /**
     * Generates an attribute wildcard property on a class.
     */
    protected static void addOtherAttributesProperty(JCodeModel jCodeModel, JDefinedClass jDefinedClass, JDefinedClass jsUtilsClass, String nameSpace) {

        log(LogLevelSetting.DEBUG, String.format("Add getOtherAttributes property to object %1$s.%2$s ...", jDefinedClass._package().name(), jDefinedClass.name()));

        final JClass parameterRef = jCodeModel.ref(Map.class).narrow(QName.class, String.class);
        final JMethod otherAttributesGetter = addNativeGetter(jCodeModel, jDefinedClass, parameterRef, "OtherAttributes", "otherAttributes");

        addSetter(jCodeModel, jDefinedClass, parameterRef, "OtherAttributes", "otherAttributes", nameSpace);
        addStaticOtherAttributesGetter(jCodeModel, jDefinedClass, otherAttributesGetter, jsUtilsClass);
        addStaticOtherAttributesSetter(jDefinedClass, packageName);
    }

    protected static void addStaticOtherAttributesGetter(final JCodeModel jCodeModel,
                                                         final JDefinedClass jDefinedClass,
                                                         final JMethod otherAttributesGetter,
                                                         final JDefinedClass jsUtilsClass) {

        log(LogLevelSetting.DEBUG, String.format("Add getOtherAttributesMap method to object %1$s.%2$s ...", jDefinedClass._package().name(), jDefinedClass.name()));

        final int mods = JMod.PUBLIC + JMod.STATIC;
        final JClass parameterRef = jCodeModel.ref(Map.class).narrow(QName.class, String.class);

        final JMethod jMethod = jDefinedClass.method(mods, parameterRef, "getOtherAttributesMap");
        final JVar instanceParam = jMethod.param(JMod.FINAL, jDefinedClass, "instance");
        final JBlock block = jMethod.body();
        final JInvocation instanceOtherAttributes = instanceParam.invoke(otherAttributesGetter);

        jMethod.annotate(JsOverlay.class);

        block._return(jsUtilsClass.staticInvoke("toAttributesMap").arg(instanceOtherAttributes));
    }

    protected static void addStaticOtherAttributesSetter(JDefinedClass jDefinedClass, String packageName) {
        log(LogLevelSetting.DEBUG, String.format("Add setOtherAttributesMap method to object %1$s.%2$s ...", jDefinedClass._package().name(), jDefinedClass.name()));
        String directString = String.format(SET_OTHER_ATTRIBUTES_TEMPLATE, jDefinedClass.name(), packageName);
        jDefinedClass.direct(directString);
    }

    protected static JClass getPropertyRef(JCodeModel jCodeModel, CPropertyInfo cPropertyInfo, String outerClass, Map<String, JClass> definedClassesMap, Map<String, String> packageModuleMap, Model model, JDefinedClass jsUtilsClass) throws ParseModelException, JClassAlreadyExistsException {
        JClass typeRef = getOrCreatePropertyRef(cPropertyInfo, outerClass, definedClassesMap, jCodeModel, packageModuleMap, model, jsUtilsClass);
        if (typeRef == null) {
            log(LogLevelSetting.WARN, "Failed to retrieve JClass for " + cPropertyInfo.getName(false) + " inside the JCodeModel");
            return null;
        }
        log(LogLevelSetting.DEBUG, typeRef.toString());
        if (cPropertyInfo.isCollection()) {
            JClass rawArrayListClass = jCodeModel.ref(JsArrayLike.class);
            return rawArrayListClass.narrow(typeRef);
        } else {
            if (!typeRef.isPrimitive()) {
                typeRef = jCodeModel.ref(typeRef.unboxify().fullName());
            }
            return typeRef;
        }
    }

    protected static JClass getOrCreatePropertyRef(CPropertyInfo cPropertyInfo, String outerClass, Map<String, JClass> definedClassesMap, JCodeModel jCodeModel, Map<String, String> packageModuleMap, Model model, JDefinedClass jsUtilsClass, JDefinedClass jsiNameClass) throws ParseModelException, JClassAlreadyExistsException {
        String originalClassName = getOriginalClassName(cPropertyInfo, outerClass);
        return getOrCreatePropertyRef(originalClassName, definedClassesMap, jCodeModel, packageModuleMap, model, packageName, !cPropertyInfo.isCollection(), jsiNameClass);
    }

    protected static JClass getOrCreatePropertyRef(String originalClassName, Map<String, JClass> definedClassesMap, JCodeModel jCodeModel, Map<String, String> packageModuleMap, Model model, JDefinedClass jsUtilsClass, boolean toUnbox, JDefinedClass jsiNameClass) throws ParseModelException, JClassAlreadyExistsException {
        JClass toReturn;
        final Optional<JClass> javaRef = getJavaRef(originalClassName, jCodeModel, toUnbox);
        if (javaRef.isPresent()) {
            toReturn = javaRef.get();
        } else {
            if (!definedClassesMap.containsKey(originalClassName)) {
                Optional<NClass> nClassKey = model.beans().keySet().stream().filter(nClass -> nClass.fullName().equals(originalClassName)).findFirst();
                Optional<NClass> nEnumKey = model.enums().keySet().stream().filter(nClass -> nClass.fullName().equals(originalClassName)).findFirst();
                if (nClassKey.isPresent()) {
                    populateJCodeModel(definedClassesMap, jCodeModel, model.beans().get(nClassKey.get()), packageModuleMap, model, jsUtilsClass, jsiNameClass);
                } else if (nEnumKey.isPresent()) {
                    populateJCodeModel(definedClassesMap, jCodeModel, model.enums().get(nEnumKey.get()));
                } else {
                    throw new ParseModelException("Failed to retrieve " + originalClassName + " inside the Model");
                }
            }
            toReturn = definedClassesMap.get(originalClassName);
        }
        return toReturn;
    }

    protected static String getOriginalClassName(CPropertyInfo cPropertyInfo, String outerClass) {
        String fullClassName = null;
        log(LogLevelSetting.DEBUG, "getClassName...");
        if (cPropertyInfo instanceof CReferencePropertyInfo) {
            final CReferencePropertyInfo cReferencePropertyInfo = (CReferencePropertyInfo) cPropertyInfo;
            final Set<CElement> elements = (cReferencePropertyInfo).getElements();
            if (!elements.isEmpty()) {
                final CElementInfo cElement = (CElementInfo) elements.toArray()[0];
                CElementPropertyInfo property = cElement.getProperty();
                fullClassName = getPropertyClassName(property);
            } else if (cReferencePropertyInfo.baseType != null) {
                fullClassName = cReferencePropertyInfo.baseType.fullName();
            }
        } else {
            fullClassName = getPropertyClassName(cPropertyInfo);
        }
        if (fullClassName == null) {
            log(LogLevelSetting.WARN, "Failed to log ref for " + cPropertyInfo.getName(false) + " that is a " + cPropertyInfo.getClass().getCanonicalName() + " defined inside " + outerClass, null);
            fullClassName = "java.lang.Object";
        }
        if (fullClassName.equals("javax.xml.datatype.XMLGregorianCalendar")) {
            fullClassName = "java.util.Date";
        }
        return fullClassName;
    }

    protected static String getPropertyClassName(CPropertyInfo toLog) {
        String toReturn = null;
        if (!toLog.ref().isEmpty()) {
            toReturn = toLog.ref().iterator().next().getType().fullName();
            log(LogLevelSetting.DEBUG, "cPropertyInfo.ref().iterator().next().getType(): " + toReturn);
        }
        return toReturn;
    }

    protected static void addGetter(final JCodeModel jCodeModel,
                                    final JDefinedClass jDefinedClass,
                                    final JDefinedClass jsUtilsClass,
                                    final String nameSpace,
                                    final JClass propertyRef,
                                    final String publicPropertyName,
                                    final String privatePropertyName) {

        final boolean isJsArrayLike = propertyRef != null && Objects.equals(propertyRef.erasure().name(), "JsArrayLike");

        if (isJsArrayLike) {

            final String nativePropertyName = "Native" + publicPropertyName;
            final JClass propertyRefTypeParam = propertyRef.getTypeParameters().get(0);

            addNativeGetter(jCodeModel, jDefinedClass, propertyRef, nativePropertyName, privatePropertyName);
            addDefaultGetter(jCodeModel, jDefinedClass, jsUtilsClass, propertyRefTypeParam, publicPropertyName, privatePropertyName);
            addAddMethod(jCodeModel, jDefinedClass, jsUtilsClass, propertyRefTypeParam, publicPropertyName, privatePropertyName);
            addAddAllMethod(jCodeModel, jDefinedClass, jsUtilsClass, propertyRefTypeParam, publicPropertyName, privatePropertyName);
            addRemoveMethod(jCodeModel, jDefinedClass, jsUtilsClass, publicPropertyName, privatePropertyName);
        } else {
            addNativeGetter(jCodeModel, jDefinedClass, propertyRef, publicPropertyName, privatePropertyName);
        }
    }

    protected static void addAddMethod(final JCodeModel jCodeModel,
                                       final JDefinedClass jDefinedClass,
                                       final JDefinedClass jsUtilsClass,
                                       final JClass propertyRef,
                                       final String publicPropertyName,
                                       final String privatePropertyName) {
        BuilderUtils.addAddMethod(jCodeModel, jDefinedClass, jsUtilsClass, propertyRef, publicPropertyName, privatePropertyName);
    }

    protected static void addAddAllMethod(final JCodeModel jCodeModel,
                                          final JDefinedClass jDefinedClass,
                                          final JDefinedClass jsUtilsClass,
                                          final JClass propertyRef,
                                          final String publicPropertyName,
                                          final String privatePropertyName) {
        BuilderUtils.addAddAllMethod(jCodeModel, jDefinedClass, jsUtilsClass, propertyRef, publicPropertyName, privatePropertyName);
    }

    protected static void addRemoveMethod(final JCodeModel jCodeModel,
                                          final JDefinedClass jDefinedClass,
                                          final JDefinedClass jsUtilsClass,
                                          final String publicPropertyName,
                                          final String privatePropertyName) {
        BuilderUtils.addRemoveMethod(jCodeModel, jDefinedClass, jsUtilsClass, publicPropertyName, privatePropertyName);
    }

    protected static void addDefaultGetter(final JCodeModel jCodeModel,
                                           final JDefinedClass jDefinedClass,
                                           final JDefinedClass jsUtilsClass,
                                           final JClass propertyRef,
                                           final String publicPropertyName,
                                           final String privatePropertyName) {
        BuilderUtils.addDefaultGetter(jCodeModel, jDefinedClass, jsUtilsClass, propertyRef, publicPropertyName, privatePropertyName);
    }

    protected static JMethod addNativeGetter(final JCodeModel jCodeModel,
                                             final JDefinedClass jDefinedClass,
                                             final JClass propertyRef,
                                             final String publicPropertyName,
                                             final String privatePropertyName) {
        return BuilderUtils.addNativeGetter(jCodeModel, jDefinedClass, propertyRef, publicPropertyName, privatePropertyName);
    }

    protected static void addSetter(JCodeModel jCodeModel, JDefinedClass jDefinedClass, JClass propertyRef, String
            publicPropertyName, String privatePropertyName, String namespace) {
        BuilderUtils.addSetter(jCodeModel, jDefinedClass, propertyRef, publicPropertyName, privatePropertyName);
    }

    protected static JClass parseClass(String className,
                                       JCodeModel codeModel,
                                       Map<String, JClass> definedClassesMap) {
        return new JavaTypeParser(definedClassesMap).parseClass(className, codeModel);
    }

    protected static String getJNIRepresentation(JDefinedClass toConvert) {
        String className = toConvert.name();
        if (toConvert.outer() != null) {
            className = toConvert.outer().name() + "$" + className;
        }
        String toReturn = "L" + toConvert._package().name().replace('.', '/') + "/" + className;
        return toReturn;
    }
}
