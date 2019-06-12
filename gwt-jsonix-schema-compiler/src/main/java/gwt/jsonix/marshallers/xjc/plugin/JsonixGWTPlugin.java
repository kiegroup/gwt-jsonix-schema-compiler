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

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JCommentPart;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.writer.FileCodeWriter;
import com.sun.codemodel.writer.FilterCodeWriter;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CAttributePropertyInfo;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CNonElement;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.model.CValuePropertyInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Outline;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

/**
 * Wrapper class of the original <code>JsonixPlugin</code> that also generates <b>JSInterop</b> code
 */
public class JsonixGWTPlugin extends Plugin {

    public static final String OPTION_NAME = "Xgwtjsonix";
    public static final String OPTION = "-" + OPTION_NAME;

    private Map<String, JDefinedClass> definedClassesMap = new HashMap<>();

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    @Override
    public String getUsage() {
        return "  -Xgwtjsonix :  Generates Jsonix mappings and JSInterop code.\n"
                + "                    See (to_be_defined)";
    }

    @Override
    public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) throws SAXException {
        return true;
    }

    @Override
    public void postProcessModel(Model model, ErrorHandler errorHandler) {
        try {
            generateJSInteropCode(model, errorHandler);
        } catch (Exception e) {
            log(Level.SEVERE, e.getMessage(), e);
        }
    }

    protected void generateJSInteropCode(Model model, ErrorHandler errorHandler) throws Exception {
        log(Level.FINE, "Generating  JSInterop code...", null);
        JCodeModel jCodeModel = new JCodeModel();
        for (CClassInfo cClassInfo : model.beans().values()) {
            populateJCodeModel(jCodeModel, cClassInfo);
        }
        try {
            writeJSInteropCode(jCodeModel, createCodeWriter(model));
        } catch (Exception e) {
            errorHandler.fatalError(new SAXParseException("Failed to generate JSInterop code", new LocatorImpl(), e));
        }
    }

    protected CodeWriter createCodeWriter(Model model) throws Exception {
        final File parentDir = model.options.targetDir.getParentFile();
        File destDir = new File(parentDir.getAbsolutePath() + File.separator + "jsinterop");
        if (!destDir.exists()) {
            if (!destDir.mkdir()) {
                throw new Exception("Failed to create target dir: " + destDir.getAbsolutePath());
            }
        }
        try {
            return new FileCodeWriter(destDir, model.options.readOnly, model.options.encoding);
        } catch (IOException e) {
            throw new Exception("Failed to FileCodeWriter", e);
        }
    }

    protected void populateJCodeModel(JCodeModel toPopulate, CClassInfo cClassInfo) throws Exception {
        log(Level.FINE, "Generating  JCode model...", null);
        final CClassInfoParent parent = cClassInfo.parent();
        final JDefinedClass jDefinedClass;
        if (parent != null && definedClassesMap.containsKey(parent.fullName())) {
            jDefinedClass = definedClassesMap.get(parent.fullName())._class(cClassInfo.shortName);
        } else {
            jDefinedClass = toPopulate._class(cClassInfo.fullName());
        }
        definedClassesMap.put(cClassInfo.fullName(), jDefinedClass);
        JDocComment comment = jDefinedClass.javadoc();
        String commentString = "JSInterop adapter for <code>" + cClassInfo.shortName + "</code>";
        comment.append(commentString);
        jDefinedClass.annotate(toPopulate.ref(JsType.class)).param("isNative", true).param("namespace", toPopulate.ref(JsPackage.class).staticRef("GLOBAL"));
        for (CPropertyInfo cPropertyInfo : cClassInfo.getProperties()) {
            addProperty(toPopulate, jDefinedClass, cPropertyInfo);
        }
    }

    protected void addProperty(JCodeModel jCodeModel, JDefinedClass jDefinedClass, CPropertyInfo cPropertyInfo) throws Exception {
        final JClass propertyRef = getPropertyRef(jCodeModel, cPropertyInfo, jDefinedClass.fullName());
        final String publicPropertyName = cPropertyInfo.getName(true);
        final String privatePropertyName = cPropertyInfo.getName(false);
        addGetter(jCodeModel, jDefinedClass, propertyRef, publicPropertyName, privatePropertyName);
        addSetter(jCodeModel, jDefinedClass, propertyRef, publicPropertyName, privatePropertyName);
    }

    protected JClass getPropertyRef(JCodeModel jCodeModel, CPropertyInfo cPropertyInfo, String outerClass) throws Exception {
        String fullClassName = getClassName(cPropertyInfo, outerClass);
        JClass typeRef = jCodeModel.ref(fullClassName);
        getLog().info(typeRef.toString());
        if (cPropertyInfo.isCollection()) {
            return typeRef.array();
        } else {
            return typeRef;
        }
    }

    protected String getClassName(CPropertyInfo cPropertyInfo, String outerClass) throws Exception {
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
                    if (target.getType() != null) {
                        fullClassName = target.getType().fullName();
                    } else if (target instanceof CClassInfo) {
                        fullClassName = ((CClassInfo) target).fullName();
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
        } else if (cPropertyInfo instanceof CReferencePropertyInfo) {
            CReferencePropertyInfo cReferencePropertyInfo = (CReferencePropertyInfo) cPropertyInfo;
            fullClassName = cReferencePropertyInfo.getSchemaType().toString();
        }
        if (fullClassName == null) {
            throw new Exception("Failed to retrieve className for " + cPropertyInfo.getName(true));
        }
        if (outerClass != null) {
            fullClassName = fullClassName.replace(outerClass + ".", "");
        }
        if (fullClassName.equals("javax.xml.datatype.XMLGregorianCalendar")) {
            fullClassName = "java.util.Date";
        }
        return fullClassName;
    }

    protected void addGetter(JCodeModel jCodeModel, JDefinedClass jDefinedClass, JClass propertyRef, String
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
        getterMethod.annotate(jCodeModel.ref(JsProperty.class));
    }

    protected void addSetter(JCodeModel jCodeModel, JDefinedClass jDefinedClass, JClass propertyRef, String
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
        setterMethod.annotate(jCodeModel.ref(JsProperty.class));
    }

    protected void writeJSInteropCode(JCodeModel jCodeModel, CodeWriter baseCodeWriter) throws Exception {
        log(Level.FINE, MessageFormat.format("Writing JSInterop with [{0}].", baseCodeWriter.toString()), null);
        try {
            final CodeWriter codeWriter = new FilterCodeWriter(baseCodeWriter);
            jCodeModel.build(codeWriter);
        } catch (IOException e) {
            throw new Exception("Unable to write files: "
                                        + e.getMessage(), e);
        }
    }

    protected void log(Level level, String message, Exception e) {
        if (e != null) {
            getLog().log(level, message, e);
        } else {
            getLog().log(level, message);
        }
    }

    protected Logger getLog() {
        return Logger.getLogger(JsonixGWTPlugin.class.getName());
    }
}
