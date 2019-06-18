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
import java.util.logging.Level;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JCommentPart;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.tools.xjc.model.CAttributePropertyInfo;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CNonElement;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.model.CValuePropertyInfo;
import com.sun.tools.xjc.model.Model;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

import static gwt.jsonix.marshallers.xjc.plugin.BuilderUtils.log;

/**
 * Actual builder for <b>JSInterop</b> models
 */
public class ModelBuilder {

    /**
     * Method to create the <b>JSInterop</b> representation oif <b>xsd</b> definitions
     * @param definedClassesMap
     * @param model
     * @param jCodeModel
     * @throws Exception
     */
    public static void generateJSInteropModels(Map<String, JDefinedClass> definedClassesMap, Model model, JCodeModel jCodeModel) throws Exception {
        definedClassesMap.clear();
        log(Level.FINE, "Generating  JSInterop code...", null);
        for (CClassInfo cClassInfo : model.beans().values()) {
            populateJCodeModel(definedClassesMap, jCodeModel, cClassInfo);
        }
    }

    protected static void populateJCodeModel(Map<String, JDefinedClass> definedClassesMap, JCodeModel toPopulate, CClassInfo cClassInfo) throws Exception {
        log(Level.FINE, "Generating  JCode model...", null);
        final CClassInfoParent parent = cClassInfo.parent();
        final JDefinedClass jDefinedClass;
        if (parent != null && definedClassesMap.containsKey(parent.fullName())) {
            int mod = JMod.PUBLIC + JMod.STATIC;
            jDefinedClass = definedClassesMap.get(parent.fullName())._class(mod, cClassInfo.shortName);
        } else {
            String fullClassName = cClassInfo.getOwnerPackage().name() + ".JSI" + cClassInfo.shortName;
            jDefinedClass = toPopulate._class(fullClassName);
        }
        definedClassesMap.put(cClassInfo.fullName(), jDefinedClass);
        JDocComment comment = jDefinedClass.javadoc();
        String commentString = "JSInterop adapter for <code>" + cClassInfo.shortName + "</code>";
        comment.append(commentString);
        jDefinedClass.annotate(toPopulate.ref(JsType.class))
                .param("isNative", true)
                .param("namespace", toPopulate.ref(JsPackage.class).staticRef("GLOBAL"))
                .param("name", cClassInfo.shortName);
        for (CPropertyInfo cPropertyInfo : cClassInfo.getProperties()) {
            addProperty(toPopulate, jDefinedClass, cPropertyInfo, definedClassesMap);
        }
    }

    protected static void addProperty(JCodeModel jCodeModel, JDefinedClass jDefinedClass, CPropertyInfo cPropertyInfo, Map<String, JDefinedClass> definedClassesMap) throws Exception {
        final JClass propertyRef = getPropertyRef(jCodeModel, cPropertyInfo, jDefinedClass.fullName(), definedClassesMap);
        final String publicPropertyName = cPropertyInfo.getName(true);
        final String privatePropertyName = cPropertyInfo.getName(false);
        addGetter(jCodeModel, jDefinedClass, propertyRef, publicPropertyName, privatePropertyName);
        addSetter(jCodeModel, jDefinedClass, propertyRef, publicPropertyName, privatePropertyName);
    }

    protected static JClass getPropertyRef(JCodeModel jCodeModel, CPropertyInfo cPropertyInfo, String outerClass, Map<String, JDefinedClass> definedClassesMap) throws Exception {
        String fullClassName = getClassName(cPropertyInfo, outerClass, definedClassesMap);
        JClass typeRef = jCodeModel.ref(fullClassName);
        log(Level.FINE, typeRef.toString(), null);
        if (cPropertyInfo.isCollection()) {
            return typeRef.array();
        } else {
            return typeRef;
        }
    }

    protected static String getClassName(CPropertyInfo cPropertyInfo, String outerClass, Map<String, JDefinedClass> definedClassesMap) throws Exception {
        String fullClassName = null;
        // EXPERIMENTS
        if (cPropertyInfo instanceof CAttributePropertyInfo) {
            CAttributePropertyInfo cAttributePropertyInfo = (CAttributePropertyInfo) cPropertyInfo;
            final CNonElement target = cAttributePropertyInfo.getTarget();
            if (target != null) {
                if (target.getType() != null) {
                    fullClassName = target.getType().fullName();
                } else if (target instanceof CClassInfo) {
                    fullClassName = ((CClassInfo) target).fullName();
                } else if (target.getTypeName() != null) {
                    fullClassName = target.getTypeName().toString();
                }
            } else {
                fullClassName = cAttributePropertyInfo.getXmlName().toString();
            }
        } else if (cPropertyInfo instanceof CElementPropertyInfo) {
            CElementPropertyInfo cElementPropertyInfo = (CElementPropertyInfo) cPropertyInfo;
            if (!cElementPropertyInfo.getTypes().isEmpty()) {
                final CTypeRef cTypeRef = cElementPropertyInfo.getTypes().get(0);
                final CNonElement target = cTypeRef.getTarget();
                if (target != null) {
                    if (target instanceof CClassInfo && ((CClassInfo) target).parent() != null  && definedClassesMap.containsKey(((CClassInfo) target).parent().fullName())) {
                        fullClassName = definedClassesMap.get(((CClassInfo) target).parent().fullName()).fullName() + "." + cPropertyInfo.getName(true);
                    } else if (target.getType() != null) {
                        fullClassName = target.getType().fullName();
                    } else if (target.getTypeName() != null) {
                        fullClassName = target.getTypeName().toString();
                    }
                } else if (cTypeRef.getTypeName() != null) {
                    fullClassName = cTypeRef.getTypeName().toString();
                } else if (cElementPropertyInfo.getSchemaType() != null) {
                    fullClassName = cElementPropertyInfo.getSchemaType().toString();
                }
            } else if (!cElementPropertyInfo.ref().isEmpty()) {
                final CNonElement cNonElement = cElementPropertyInfo.ref().get(0);
                fullClassName = cNonElement.getTypeName().toString();
            } else {
                fullClassName = cElementPropertyInfo.getSchemaType().toString();
            }
        } else if (cPropertyInfo instanceof CValuePropertyInfo) {
            CValuePropertyInfo cValuePropertyInfo = (CValuePropertyInfo) cPropertyInfo;
            final CNonElement cNonElement = cValuePropertyInfo.getTarget();
            fullClassName = cNonElement.getTypeName().toString();
        } else if (cPropertyInfo instanceof CReferencePropertyInfo && cPropertyInfo.getSchemaType() != null) {
            fullClassName = cPropertyInfo.getSchemaType().toString();
        }
        if (fullClassName == null) {
            throw new Exception("Failed to retrieve className for " + cPropertyInfo.getName(true));
        }
        if (outerClass != null) {
            fullClassName = cleanupFullClassName(outerClass, fullClassName);
        }
        if (fullClassName.equals("javax.xml.datatype.XMLGregorianCalendar")) {
            fullClassName = "java.util.Date";
        }
        return fullClassName;
    }

    protected static void addGetter(JCodeModel jCodeModel, JDefinedClass jDefinedClass, JClass propertyRef, String
            publicPropertyName, String privatePropertyName) {
        String getterMethodName = "get" + publicPropertyName;
        int mod = JMod.PUBLIC + JMod.FINAL + JMod.NATIVE;
        JMethod getterMethod = jDefinedClass.method(mod, propertyRef, getterMethodName);
        JDocComment getterComment = getterMethod.javadoc();
        String commentString = "Getter for <b>" + privatePropertyName + "</b>";
        getterComment.append(commentString);
        JCommentPart getterCommentReturnPart = getterComment.addReturn();
        commentString = " <b>" + privatePropertyName + "</<b>";
        getterCommentReturnPart.add(commentString);
        getterMethod.annotate(jCodeModel.ref(JsProperty.class)).param("name", privatePropertyName);
    }

    protected static void addSetter(JCodeModel jCodeModel, JDefinedClass jDefinedClass, JClass propertyRef, String
            publicPropertyName, String privatePropertyName) {
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
        setterMethod.annotate(jCodeModel.ref(JsProperty.class)).param("name", privatePropertyName);
    }

    protected static String cleanupFullClassName(String outerClassName, String fullClassName) {
        String toReturn = fullClassName.replace(outerClassName + ".", "");
        if (outerClassName.contains(".") && fullClassName.contains(".")) {
            String outerClassNamePackage = outerClassName.substring(0, outerClassName.lastIndexOf("."));
            String fullClassNamePackage = fullClassName.substring(0, fullClassName.lastIndexOf("."));
            if (Objects.equals(outerClassNamePackage, fullClassNamePackage)) {
                toReturn = "JSI" + fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            }
        }
        return toReturn;
    }
}
