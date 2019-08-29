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
import java.util.Optional;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JCommentPart;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.writer.FileCodeWriter;
import com.sun.codemodel.writer.FilterCodeWriter;
import com.sun.tools.xjc.model.Model;
import gwt.jsonix.marshallers.xjc.plugin.GWTSettings;
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

    public static JAnnotationUse addGetter(JCodeModel jCodeModel, JDefinedClass jDefinedClass, JClass propertyRef, String
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
        return getterMethod.annotate(jCodeModel.ref(JsProperty.class)).param("name", privatePropertyName);
    }

    public static JAnnotationUse addSetter(JCodeModel jCodeModel, JDefinedClass jDefinedClass, JClass propertyRef, String
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
        commentString = " <b>" + privatePropertyName + "</<b> to set.";
        setterCommentParameterPart.add(commentString);
        return setterMethod.annotate(jCodeModel.ref(JsProperty.class)).param("name", privatePropertyName);
    }

    public static Optional<JClass> getJavaRef(String originalClassName, JCodeModel jCodeModel) {
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
