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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JCommentPart;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JEnumConstant;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
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
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsArrayLike;
import org.hisrc.jsonix.settings.LogLevelSetting;
import org.jvnet.jaxb2_commons.plugin.inheritance.Customizations;
import org.jvnet.jaxb2_commons.plugin.inheritance.ExtendsClass;
import org.jvnet.jaxb2_commons.plugin.inheritance.ExtendsClassReader;
import org.jvnet.jaxb2_commons.plugin.inheritance.util.JavaTypeParser;
import org.jvnet.jaxb2_commons.util.CustomizationUtils;

import static gwt.jsonix.marshallers.xjc.plugin.BuilderUtils.log;
import static org.jvnet.jaxb2_commons.plugin.inheritance.Customizations.EXTENDS_ELEMENT_NAME;

/**
 * Actual builder for <b>JSInterop</b> models
 */
public class ModelBuilder {

    protected static final String newInstanceTemplate = "\n\n\n\npublic static native %1$s newInstance() /*-{\n" +
            "        var json = \"{\\\"TYPE_NAME\\\": \\\"%2$s\\\"}\";\n" +
            "        var retrieved = JSON.parse(json)\n" +
            "        return retrieved\n" +
            "    }-*/;";

    /**
     * Method to create the <b>JSInterop</b> representation oif <b>xsd</b> definitions
     * @param definedClassesMap
     * @param model
     * @param jCodeModel
     * @param packageModuleMap
     * @throws Exception
     */
    public static void generateJSInteropModels(Map<String, JClass> definedClassesMap, Model model, JCodeModel jCodeModel, Map<String, String> packageModuleMap) throws Exception {
        definedClassesMap.clear();
        log(LogLevelSetting.DEBUG, "Generating JSInterop code...", null);
        for (CClassInfo cClassInfo : model.beans().values()) {
            populateJCodeModel(definedClassesMap, jCodeModel, cClassInfo, packageModuleMap, model);
        }
    }

    protected static void populateJCodeModel(Map<String, JClass> definedClassesMap, JCodeModel toPopulate, CClassInfo cClassInfo, Map<String, String> packageModuleMap, Model model) throws Exception {
        log(LogLevelSetting.DEBUG, "Generating  JCode model...", null);
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
            final ExtendsClass extendsClass = (ExtendsClass) CustomizationUtils.unmarshall(Customizations.getContext(),
                                                                                           extendsClassCustomization);
            final String extendsClassName = ExtendsClassReader.getValue(extendsClass);
            jDefinedBaseClass = parseClass(extendsClassName, toPopulate, definedClassesMap);
        }

        if (basecClassInfo != null) { // This is the "extended" class
            if (!definedClassesMap.containsKey(basecClassInfo.fullName())) {
                populateJCodeModel(definedClassesMap, toPopulate, basecClassInfo, packageModuleMap, model);
            }
            jDefinedBaseClass = definedClassesMap.get(basecClassInfo.fullName());
        }
        boolean hasClassParent = (parent != null && !(parent instanceof CClassInfoParent.Package));
        String parentNamespace = null;
        if (hasClassParent && definedClassesMap.containsKey(parent.fullName())) { // This is for inner classes
            int mod = JMod.PUBLIC + JMod.STATIC;
            String parentFullName = parent.fullName();
            jDefinedClass = jDefinedBaseClass != null ? ((JDefinedClass) definedClassesMap.get(parentFullName))._class(mod, "JSI" + nameSpace)._extends(jDefinedBaseClass) : ((JDefinedClass) definedClassesMap.get(parentFullName))._class(mod, "JSI" + nameSpace);
            parentNamespace = parentFullName.contains(".") ? parentFullName.substring(parentFullName.lastIndexOf(".") + 1) : parentFullName;
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
        jDefinedClass.annotate(toPopulate.ref(JsType.class))
                .param("namespace", nameSpaceExpression)
                .param("name", shortClassName);
        String moduleName = packageModuleMap.get(jDefinedClass._package().name());
        addNewInstance(jDefinedClass, moduleName, nameSpace);
        addTypeName(jDefinedClass, toPopulate, moduleName, nameSpace);
        if (basecClassInfo == null) {
            addGetTypeNameProperty(toPopulate, jDefinedClass, nameSpace);
        }
        for (CPropertyInfo cPropertyInfo : cClassInfo.getProperties()) {
            addProperty(toPopulate, jDefinedClass, cPropertyInfo, definedClassesMap, packageModuleMap, model, nameSpace);
        }
    }

    protected static void populateJCodeModel(Map<String, JClass> definedClassesMap, JCodeModel toPopulate, CEnumLeafInfo cEnumLeafInfo) throws Exception {
        log(LogLevelSetting.DEBUG, "Generating  JCode model...", null);
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

    protected static void addEnumValueField(JCodeModel toPopulate, JDefinedClass jDefinedClass) {
        final JClass propertyRef = getJavaRef(String.class.getCanonicalName(), toPopulate).get();
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

    protected static void addNewInstance(JDefinedClass jDefinedClass, String moduleName, String originalName) {
        String fullName = moduleName + "." + originalName;
        String directString = String.format(newInstanceTemplate, jDefinedClass.name(), fullName);
        jDefinedClass.direct(directString);
    }

    protected static void addTypeName(JDefinedClass jDefinedClass, JCodeModel jCodeModel, String moduleName, String originalName) {
        final JClass propertyRef = getJavaRef(String.class.getCanonicalName(), jCodeModel).get();
        String fullName = moduleName + "." + originalName;
        int mods = JMod.PUBLIC + JMod.STATIC + JMod.FINAL;
        final JFieldVar typeNameField = jDefinedClass.field(mods, propertyRef, "TYPE");
        typeNameField.init(JExpr.lit(fullName));
    }


    protected static void addGetTypeNameProperty(JCodeModel jCodeModel, JDefinedClass jDefinedClass, String namespace) {
        log(LogLevelSetting.DEBUG, String.format("Add getTYPENAME property to object %1$s.%2$s ...", jDefinedClass._package().name(), jDefinedClass.name()), null);
        JClass parameterRef = jCodeModel.ref(String.class);
        addGetter(jCodeModel, jDefinedClass, parameterRef, "TYPE_NAME", "TYPE_NAME", namespace);
    }

    protected static void addProperty(JCodeModel jCodeModel, JDefinedClass jDefinedClass, CPropertyInfo cPropertyInfo, Map<String, JClass> definedClassesMap, Map<String, String> packageModuleMap, Model model, String namespace) throws Exception {
        final JClass propertyRef = getPropertyRef(jCodeModel, cPropertyInfo, jDefinedClass.fullName(), definedClassesMap, packageModuleMap, model);
        final String publicPropertyName = cPropertyInfo.getName(true);
        final String privatePropertyName = cPropertyInfo.getName(false);
        addGetter(jCodeModel, jDefinedClass, propertyRef, publicPropertyName, privatePropertyName, namespace);
        addSetter(jCodeModel, jDefinedClass, propertyRef, publicPropertyName, privatePropertyName, namespace);
    }

    protected static JClass getPropertyRef(JCodeModel jCodeModel, CPropertyInfo cPropertyInfo, String outerClass, Map<String, JClass> definedClassesMap, Map<String, String> packageModuleMap, Model model) throws Exception {
        JClass typeRef = getOrCreatePropertyRef(cPropertyInfo, outerClass, definedClassesMap, jCodeModel, packageModuleMap, model);
        if (typeRef == null) {
            log(LogLevelSetting.WARN, "Failed to retrieve JClass for " + cPropertyInfo.getName() + " inside the JCodeModel", null);
            return null;
        }
        log(LogLevelSetting.DEBUG, typeRef.toString(), null);
        if (cPropertyInfo.isCollection()) {
            if (typeRef._package().name().startsWith("java")) {
                return typeRef.array();
            } else {
                JClass rawArrayListClass = jCodeModel.ref(JsArrayLike.class);
                return rawArrayListClass.narrow(typeRef);
            }
        } else {
            return typeRef;
        }
    }

    protected static JClass getOrCreatePropertyRef(CPropertyInfo cPropertyInfo, String outerClass, Map<String, JClass> definedClassesMap, JCodeModel jCodeModel, Map<String, String> packageModuleMap, Model model) throws Exception {
        JClass toReturn;
        String originalClassName = getOriginalClassName(cPropertyInfo, outerClass, definedClassesMap);
        final Optional<JClass> javaRef = getJavaRef(originalClassName, jCodeModel);
        if (javaRef.isPresent()) {
            toReturn = javaRef.get();
        } else {
            if (!definedClassesMap.containsKey(originalClassName)) {
                Optional<NClass> nClassKey = model.beans().keySet().stream().filter(nClass -> nClass.fullName().equals(originalClassName)).findFirst();
                Optional<NClass> nEnumKey = model.enums().keySet().stream().filter(nClass -> nClass.fullName().equals(originalClassName)).findFirst();
                if (nClassKey.isPresent()) {
                    populateJCodeModel(definedClassesMap, jCodeModel, model.beans().get(nClassKey.get()), packageModuleMap, model);
                } else if (nEnumKey.isPresent()) {
                    populateJCodeModel(definedClassesMap, jCodeModel, model.enums().get(nEnumKey.get()));
                } else {
                    throw new Exception("Failed to retrieve " + originalClassName + " inside the Model");
                }
            }
            toReturn = definedClassesMap.get(originalClassName);
        }
        return toReturn;
    }

    protected static Optional<JClass> getJavaRef(String originalClassName, JCodeModel jCodeModel) {
        Optional<JClass> toReturn = Optional.empty();
        try {
            final Class<?> aClass = Class.forName(originalClassName);
            if (originalClassName.startsWith("java")) {
                JClass ref = jCodeModel.ref(aClass);
                if (!ref.isPrimitive()) {
                    ref = jCodeModel.ref(ref.unboxify().fullName());
                }
                toReturn = Optional.ofNullable(ref);
            }
        } catch (ClassNotFoundException e) {
            // ignore
        }
        return toReturn;
    }

    protected static String getOriginalClassName(CPropertyInfo cPropertyInfo, String outerClass, Map<String, JClass> definedClassesMap) throws Exception {
        String fullClassName = null;
        log(LogLevelSetting.DEBUG, "getClassName...", null);
        if (cPropertyInfo instanceof CReferencePropertyInfo) {
            final Set<CElement> elements = ((CReferencePropertyInfo) cPropertyInfo).getElements();
            if (!elements.isEmpty()) {
                final CElementInfo cElement = (CElementInfo) elements.toArray()[0];
                CElementPropertyInfo property = cElement.getProperty();
                fullClassName = getPropertyClassName(property);
            }
        } else {
            fullClassName = getPropertyClassName(cPropertyInfo);
        }
        if (fullClassName == null) {
            log(LogLevelSetting.WARN, "Failed to log ref for " + cPropertyInfo.getName() + " that is a " + cPropertyInfo.getClass().getCanonicalName() + " defined inside " + outerClass, null);
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
            log(LogLevelSetting.DEBUG, "cPropertyInfo.ref().iterator().next().getType(): " + toReturn, null);
        }
        return toReturn;
    }

    protected static void addGetter(JCodeModel jCodeModel, JDefinedClass jDefinedClass, JClass propertyRef, String
            publicPropertyName, String privatePropertyName, String namespace) {
        String getterMethodName = "get" + publicPropertyName;
        int mod = JMod.PUBLIC + JMod.FINAL + JMod.NATIVE;
        JMethod getterMethod = jDefinedClass.method(mod, propertyRef, getterMethodName);
        JDocComment getterComment = getterMethod.javadoc();
        String commentString = "Getter for <b>" + privatePropertyName + "</b>";
        getterComment.append(commentString);
        JCommentPart getterCommentReturnPart = getterComment.addReturn();
        commentString = " <b>" + privatePropertyName + "</<b>";
        getterCommentReturnPart.add(commentString);
        final JAnnotationUse name = getterMethod.annotate(jCodeModel.ref(JsProperty.class)).param("name", privatePropertyName);
        conditionalAddNamespaceToProperty(name, propertyRef, namespace);
    }

    protected static void addSetter(JCodeModel jCodeModel, JDefinedClass jDefinedClass, JClass propertyRef, String
            publicPropertyName, String privatePropertyName, String namespace) {
        String setterMethodName = "set" + publicPropertyName;
        int mod = JMod.PUBLIC + JMod.FINAL + JMod.NATIVE;
        JMethod setterMethod = jDefinedClass.method(mod, Void.TYPE, setterMethodName);
        setterMethod.param(propertyRef, privatePropertyName);
        JDocComment setterComment = setterMethod.javadoc();
        String commentString = "Setter for <b>" + privatePropertyName + "</b>";
        setterComment.append(commentString);
        JCommentPart setterCommentParameterPart = setterComment.addParam(privatePropertyName);
        commentString = " <b>" + privatePropertyName + "</<b> to set.";
        setterCommentParameterPart.add(commentString);
        final JAnnotationUse name = setterMethod.annotate(jCodeModel.ref(JsProperty.class)).param("name", privatePropertyName);
        conditionalAddNamespaceToProperty(name, propertyRef, namespace);
    }

    protected static void conditionalAddNamespaceToProperty(JAnnotationUse annotationUse, JClass propertyRef, String nameSpace) {
        String fullName = propertyRef.isArray() ? propertyRef.elementType().fullName() : propertyRef.fullName();
        if (!fullName.equals(Object.class.getCanonicalName())) {
            annotationUse.param("namespace", nameSpace);
        }
    }

    protected static JClass parseClass(String _class,
                                       JCodeModel codeModel,
                                       Map<String, JClass> definedClassesMap) {
        return new JavaTypeParser(definedClassesMap).parseClass(_class, codeModel);
    }
}
