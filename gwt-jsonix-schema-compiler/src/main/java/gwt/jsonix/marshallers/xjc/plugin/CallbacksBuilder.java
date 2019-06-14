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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import jsinterop.annotations.JsFunction;
import org.apache.commons.lang3.StringUtils;

import static gwt.jsonix.marshallers.xjc.plugin.BuilderUtils.CALLBACKS;
import static gwt.jsonix.marshallers.xjc.plugin.BuilderUtils.MARSHALL_CALLBACK;
import static gwt.jsonix.marshallers.xjc.plugin.BuilderUtils.UNMARSHALL_CALLBACK;
import static gwt.jsonix.marshallers.xjc.plugin.BuilderUtils.log;

/**
 * Actual builder for the <b>JSInterop</b> <b>callbacks</b> classes
 */
public class CallbacksBuilder {

    /**
     * Method to create the <b>JSInterop</b> <b>callbacks</b> classes
     * @param containersClasses
     * @param jCodeModel
     * @return
     * @throws Exception
     */
    public static Map<String, Map<String, JDefinedClass>> generateJSInteropCallbacks(final List<JDefinedClass> containersClasses, JCodeModel jCodeModel) throws Exception {
        log(Level.FINE, "Generating  JSInterop callbacks ...", null);
        Map<String, Map<String, JDefinedClass>> toReturn = new HashMap<>();
        for (JDefinedClass containerClass : containersClasses) {
            String basePackage = containerClass._package().name();
            if (basePackage.contains(".")) {
                basePackage = basePackage.substring(0, basePackage.lastIndexOf("."));
            }
            basePackage += "." + CALLBACKS;
            final JDefinedClass unMarshallCallback = createUnMarshallCallback(jCodeModel, containerClass, basePackage);
            final JDefinedClass marshallCallback = createMarshallCallback(jCodeModel, containerClass.name(), basePackage);
            Map<String, JDefinedClass> innerMap = new HashMap<>();
            innerMap.put(UNMARSHALL_CALLBACK, unMarshallCallback);
            innerMap.put(MARSHALL_CALLBACK, marshallCallback);
            toReturn.put(containerClass.name(), innerMap);
        }
        return toReturn;
    }

    /**
     * @param toPopulate
     * @param jDefinedClass
     * @param basePackage
     * @throws Exception
     */
    protected static JDefinedClass createUnMarshallCallback(JCodeModel toPopulate, JDefinedClass jDefinedClass, String basePackage) throws Exception {
        String callbackName = jDefinedClass.name() + UNMARSHALL_CALLBACK;
        String comment = "Unmarshaller callback for <code>" + jDefinedClass.name() + "</code>";
        return createCallback(toPopulate, callbackName, comment, jDefinedClass, StringUtils.uncapitalize(jDefinedClass.name()), basePackage);
    }

    /**
     * @param toPopulate
     * @param containerName
     * @param basePackage
     * @return
     * @throws Exception
     */
    protected static JDefinedClass createMarshallCallback(JCodeModel toPopulate, String containerName, String basePackage) throws Exception {
        String callbackName = containerName + MARSHALL_CALLBACK;
        String comment = "Marshaller callback for <code>" + containerName + "</code>";
        JClass parameterRef = toPopulate.ref(String.class);
        String parameterName = "xmlString";
        return createCallback(toPopulate, callbackName, comment, parameterRef, parameterName, basePackage);
    }

    /**
     * @param toPopulate
     * @param callbackName
     * @param commentString
     * @param parameterRef
     * @param parameterName
     * @throws Exception
     */
    protected static JDefinedClass createCallback(JCodeModel toPopulate, String callbackName, String commentString,
                                                  JClass parameterRef, String parameterName, String basePackage) throws Exception {
        final JDefinedClass toReturn = toPopulate._class(basePackage + "." + callbackName, ClassType.INTERFACE);
        final JDocComment javadoc = toReturn.javadoc();
        javadoc.append(commentString);
        toReturn.annotate(toPopulate.ref(JsFunction.class));
        addCallEventMethod(toReturn, parameterRef, parameterName);
        return toReturn;
    }

    /**
     * @param jDefinedClass
     * @param parameterRef
     * @param parameterName
     */
    protected static void addCallEventMethod(JDefinedClass jDefinedClass,
                                             JClass parameterRef,
                                             String parameterName) {
        int mod = JMod.NONE;
        JMethod method = jDefinedClass.method(mod, Void.TYPE, "callEvent");
        method.param(parameterRef, parameterName);
    }
}
