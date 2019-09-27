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

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JCommentPart;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTypeVar;
import com.sun.codemodel.JVar;
import com.sun.codemodel.writer.FileCodeWriter;
import com.sun.codemodel.writer.FilterCodeWriter;
import com.sun.tools.xjc.model.Model;
import gwt.jsonix.marshallers.xjc.plugin.GWTSettings;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsProperty;
import org.hisrc.jsonix.settings.LogLevelSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to provide common methods
 */
public class BuilderUtils {

    public static final String CALLBACKS = "callbacks";
    public static final String MAIN_JS = "MainJs";
    public static final String MARSHALL_CALLBACK = "MarshallCallback";
    public static final String UNMARSHALL_CALLBACK = "UnmarshallCallback";

    private BuilderUtils() {
    }

    /**
     * Returns a <code>CodeWriter</code> whose target directory will be  Model.options.targetDir
     * @param model
     * @param settings
     * @return
     * @throws Exception
     */
    public static CodeWriter createCodeWriter(Model model, GWTSettings settings) throws IOException {
        try {
            File targetDir = settings.getJsinteropDirectory() != null ? settings.getJsinteropDirectory() : model.options.targetDir;
            return new FileCodeWriter(targetDir, model.options.readOnly, model.options.encoding);
        } catch (IOException e) {
            throw new IOException("Failed to FileCodeWriter", e);
        }
    }

    /**
     * Actually write the generated classes, using a {@link FilterCodeWriter} instantiated from the given <code>CodeWriter</code>
     * @param jCodeModel
     * @param baseCodeWriter
     * @throws Exception
     */
    public static void writeJSInteropCode(JCodeModel jCodeModel, CodeWriter baseCodeWriter) throws IOException {
        log(LogLevelSetting.DEBUG, MessageFormat.format("Writing JSInterop with [{0}].", baseCodeWriter));
        try {
            final CodeWriter codeWriter = new FilterCodeWriter(baseCodeWriter);
            jCodeModel.build(codeWriter);
        } catch (IOException e) {
            throw new IOException("Unable to write files: "
                                          + e.getMessage(), e);
        }
    }

    /**
     * Method used to <b>get</b> List
     * @param jCodeModel
     * @param jDefinedClass
     * @param jsUtilsClass
     * @param propertyRef
     * @param publicPropertyName
     * @param privatePropertyName
     * @return
     */
    public static JAnnotationUse addListGetter(final JCodeModel jCodeModel,
                                               final JDefinedClass jDefinedClass,
                                               final JDefinedClass jsUtilsClass,
                                               final JClass propertyRef,
                                               final String publicPropertyName,
                                               final String privatePropertyName) {

        final String getterMethodName = "get" + publicPropertyName;
        final int mod = JMod.PUBLIC + JMod.FINAL;
        final JClass listPropertyRef = jCodeModel.ref(List.class).narrow(propertyRef);
        final JMethod getterMethod = jDefinedClass.method(mod, listPropertyRef, getterMethodName);
        final JDocComment getterComment = getterMethod.javadoc();
        final JCommentPart getterCommentReturnPart = getterComment.addReturn();
        final JBlock body = getterMethod.body();

        getterComment.append("READ-ONLY getter for <b>" + privatePropertyName + "</b> as a {@link List}");
        getterCommentReturnPart.add("The <b>" + privatePropertyName + "</b> mapped as a {@link List}");


        final JInvocation nativeGetterInvocation = JExpr.invoke("getNative" + publicPropertyName);
        final JInvocation getUnwrappedElementsArrayInvocation = jsUtilsClass.staticInvoke("getUnwrappedElementsArray").arg(nativeGetterInvocation);
        final JInvocation list = jsUtilsClass.staticInvoke("toList").arg(getUnwrappedElementsArrayInvocation);

        body._return(list);

        return getterMethod.annotate(jCodeModel.ref(JsOverlay.class));
    }

    /**
     * Method used to set <code>List</code>
     * @param jCodeModel
     * @param jDefinedClass
     * @param jsUtilsClass
     * @param propertyRef
     * @param publicPropertyName
     * @param privatePropertyName
     * @return
     */
    public static JAnnotationUse addListSetter(final JCodeModel jCodeModel,
                                               final JDefinedClass jDefinedClass,
                                               final JDefinedClass jsUtilsClass,
                                               final JClass propertyRef,
                                               final String publicPropertyName,
                                               final String privatePropertyName) {

        final String setterMethodName = "set" + publicPropertyName;
        final int mod = JMod.PUBLIC + JMod.FINAL;
        final JClass listPropertyRef = jCodeModel.ref(List.class).narrow(propertyRef);
        final JMethod setterMethod = jDefinedClass.method(mod, void.class, setterMethodName);
        String parameterName = privatePropertyName + "Param";
        final JVar setterParam = setterMethod.param(listPropertyRef, parameterName);

        final JDocComment setterComment = setterMethod.javadoc();
        final JCommentPart setterCommentReturnPart = setterComment.addParam(parameterName);
        final JBlock body = setterMethod.body();

        setterComment.append("Setter for <b>" + privatePropertyName + "</b> as a {@link List}");
        setterCommentReturnPart.add("The <b>" + privatePropertyName + "</b> mapped as a {@link List}");

        final JInvocation toJsArrayLikeInvocation = jsUtilsClass.staticInvoke("toJsArrayLike").arg(setterParam);
        body.invoke("setNative" + publicPropertyName).arg(toJsArrayLikeInvocation);

        return setterMethod.annotate(jCodeModel.ref(JsOverlay.class));
    }

    public static JAnnotationUse addAddMethod(final JCodeModel jCodeModel,
                                              final JDefinedClass jDefinedClass,
                                              final JDefinedClass jsUtilsClass,
                                              final JClass propertyRef,
                                              final String publicPropertyName,
                                              final String privatePropertyName) {

        final String getterMethodName = "add" + publicPropertyName;
        final int mod = JMod.PUBLIC + JMod.FINAL;
        final JMethod addMethod = jDefinedClass.method(mod, Void.TYPE, getterMethodName);
        final JTypeVar typeParam = addMethod.generify("D", propertyRef);
        final JVar elementParam = addMethod.param(JMod.FINAL, typeParam, "element");
        final JDocComment addMethodComment = addMethod.javadoc();
        final JBlock body = addMethod.body();

        addMethodComment.append("Appends the specified element to the end of <b>" + privatePropertyName + "</b>");
        addMethodComment.addParam("element to be appended to <b>" + privatePropertyName + "</b>");

        final JInvocation nativeGetterInvocation = JExpr.invoke("getNative" + publicPropertyName);
        final JInvocation nativeSetterInvocation = JExpr.invoke("setNative" + publicPropertyName);
        final JExpression isNullNativeArray = nativeGetterInvocation.eq(JExpr._null());
        final JInvocation getAddInvocation = jsUtilsClass.staticInvoke("add").arg(nativeGetterInvocation).arg(elementParam);
        final JInvocation nativeArray = jsUtilsClass.staticInvoke("getNativeArray");

        body._if(isNullNativeArray)._then().add(nativeSetterInvocation.arg(nativeArray));
        body.add(getAddInvocation);

        return addMethod.annotate(jCodeModel.ref(JsOverlay.class));
    }

    public static JAnnotationUse addAddAllMethod(final JCodeModel jCodeModel,
                                                 final JDefinedClass jDefinedClass,
                                                 final JDefinedClass jsUtilsClass,
                                                 final JClass propertyRef,
                                                 final String publicPropertyName,
                                                 final String privatePropertyName) {

        final String addAllMethodName = "addAll" + publicPropertyName;
        final int mod = JMod.PUBLIC + JMod.FINAL;
        final JMethod addAllMethod = jDefinedClass.method(mod, Void.TYPE, addAllMethodName);
        final JTypeVar typeParam = addAllMethod.generify("D", propertyRef);
        final JVar elementsParam = addAllMethod.varParam(typeParam, "elements");
        final JDocComment addAllComment = addAllMethod.javadoc();
        final JBlock body = addAllMethod.body();

        addAllComment.append("Iterates over the specified collection of elements, and adds each element returned by the iterator\nto the end of <b>" + privatePropertyName + "</b>");
        addAllComment.addParam("elements to be appended to <b>" + privatePropertyName + "</b>");

        final JInvocation nativeGetterInvocation = JExpr.invoke("getNative" + publicPropertyName);
        final JInvocation nativeSetterInvocation = JExpr.invoke("setNative" + publicPropertyName);
        final JExpression isNullNativeArray = nativeGetterInvocation.eq(JExpr._null());
        final JInvocation getAddAllInvocation = jsUtilsClass.staticInvoke("addAll").arg(nativeGetterInvocation).arg(elementsParam);
        final JInvocation nativeArray = jsUtilsClass.staticInvoke("getNativeArray");

        body._if(isNullNativeArray)._then().add(nativeSetterInvocation.arg(nativeArray));
        body.add(getAddAllInvocation);

        return addAllMethod.annotate(jCodeModel.ref(JsOverlay.class));
    }

    public static JAnnotationUse addRemoveMethod(final JCodeModel jCodeModel,
                                                 final JDefinedClass jDefinedClass,
                                                 final JDefinedClass jsUtilsClass,
                                                 final String publicPropertyName,
                                                 final String privatePropertyName) {

        final String removeMethodName = "remove" + publicPropertyName;
        final int mod = JMod.PUBLIC + JMod.FINAL;
        final JMethod addMethod = jDefinedClass.method(mod, Void.TYPE, removeMethodName);
        final JVar indexParam = addMethod.param(JMod.FINAL, Integer.TYPE, "index");
        final JDocComment removeComment = addMethod.javadoc();
        final JBlock body = addMethod.body();

        removeComment.append("Removes the element at the specified position in the <b>" + privatePropertyName + "</b>");
        removeComment.addParam("index of the element to be removed");

        final JInvocation nativeGetterInvocation = JExpr.invoke("getNative" + publicPropertyName);
        final JInvocation getAddInvocation = jsUtilsClass.staticInvoke("remove").arg(nativeGetterInvocation).arg(indexParam);

        body.add(getAddInvocation);

        return addMethod.annotate(jCodeModel.ref(JsOverlay.class));
    }

    public static JMethod addNativeGetter(final JCodeModel jCodeModel,
                                          final JDefinedClass jDefinedClass,
                                          final JClass propertyRef,
                                          final String publicPropertyName,
                                          final String privatePropertyName) {
        String getterMethodName = "get" + publicPropertyName;
        int mod = JMod.PUBLIC + JMod.NATIVE;
        JMethod getterMethod = jDefinedClass.method(mod, propertyRef, getterMethodName);
        JDocComment getterComment = getterMethod.javadoc();
        String commentString = "Native getter for <b>" + privatePropertyName + "</b>";
        getterComment.append(commentString);
        JCommentPart getterCommentReturnPart = getterComment.addReturn();
        commentString = "The <b>" + privatePropertyName + "</b> JSON property";
        getterCommentReturnPart.add(commentString);
        getterMethod.annotate(jCodeModel.ref(JsProperty.class)).param("name", privatePropertyName);
        return getterMethod;
    }

    public static JAnnotationUse addNativeSetter(JCodeModel jCodeModel, JDefinedClass jDefinedClass, JClass propertyRef, String
            publicPropertyName, String privatePropertyName) {
        String setterMethodName = "set" + publicPropertyName;
        int mod = JMod.PUBLIC + JMod.FINAL + JMod.NATIVE;
        JMethod setterMethod = jDefinedClass.method(mod, Void.TYPE, setterMethodName);
        String parameterName = privatePropertyName + "Param";
        setterMethod.param(propertyRef, parameterName);
        JDocComment setterComment = setterMethod.javadoc();
        String commentString = "Setter for <b>" + privatePropertyName + "</b>";
        setterComment.append(commentString);
        JCommentPart setterCommentParameterPart = setterComment.addParam(parameterName);
        commentString = " <b>" + privatePropertyName + "</b> to set.";
        setterCommentParameterPart.add(commentString);
        return setterMethod.annotate(jCodeModel.ref(JsProperty.class)).param("name", privatePropertyName);
    }

    public static Optional<JClass> getJavaRef(String originalClassName, JCodeModel jCodeModel, boolean toUnbox) {
        Optional<JClass> toReturn = Optional.empty();
        try {
            final Class<?> aClass = Class.forName(originalClassName);
            if (originalClassName.startsWith("java")) {
                JClass ref = jCodeModel.ref(aClass);
                if (!ref.isPrimitive() && toUnbox) {
                    ref = jCodeModel.ref(ref.unboxify().fullName());
                }
                toReturn = Optional.ofNullable(ref);
            }
        } catch (ClassNotFoundException e) {
            // ignore
        }
        return toReturn;
    }

    /**
     * Helper <b>log</b>
     * @param level
     * @param message
     * @param e provide it to log <b>throwable</b>; could be <code>null</code>
     */
    public static void log(LogLevelSetting level, String message, Throwable e) {
        switch (level) {
            case TRACE:
                getLog().trace(message, e);
                break;
            case INFO:
                getLog().info(message, e);
                break;
            case WARN:
                getLog().warn(message, e);
                break;
            case DEBUG:
                getLog().debug(message, e);
                break;
            case ERROR:
                getLog().error(message, e);
                break;
        }
    }

    /**
     * Helper <b>log</b>
     * @param level
     * @param message
     */
    public static void log(LogLevelSetting level, String message) {
        switch (level) {
            case TRACE:
                getLog().trace(message);
                break;
            case INFO:
                getLog().info(message);
                break;
            case WARN:
                getLog().warn(message);
                break;
            case DEBUG:
                getLog().debug(message);
                break;
            case ERROR:
                getLog().error(message);
                break;
        }
    }

    /**
     * @return <code>Logger</code>
     */
    private static Logger getLog() {
        return LoggerFactory.getLogger(BuilderUtils.class.getName());
    }
}
