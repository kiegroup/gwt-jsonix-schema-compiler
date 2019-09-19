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
import java.util.Map;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.apache.commons.lang3.StringUtils;
import org.hisrc.jsonix.settings.LogLevelSetting;

import static gwt.jsonix.marshallers.xjc.plugin.builders.BuilderUtils.addGetter;
import static gwt.jsonix.marshallers.xjc.plugin.builders.BuilderUtils.addSetter;
import static gwt.jsonix.marshallers.xjc.plugin.builders.BuilderUtils.log;

/**
 * Actual builder for the <b>JSInterop</b> <code>container</code> class that will be used by <b>marshaller</b> callback
 */
public class ContainerObjectBuilder {

    private ContainerObjectBuilder() {
    }

    /**
     * Method to create the <b>JSInterop</b> <code>MainJs</code> class
     * @param packageModuleMap Map the package name with the "main" container class name
     * @param topLevelElementsMap Map the package name with all its top-level elements
     * @param jCodeModel
     * @return
     * @throws Exception
     */
    public static List<JDefinedClass> generateJSInteropContainerObjects(final Map<String, String> packageModuleMap, final Map<String, Map<String, JClass>> topLevelElementsMap, JCodeModel jCodeModel/*, JDefinedClass jsiNameClass, JDefinedClass jsUtilsClass*/) throws JClassAlreadyExistsException {
        log(LogLevelSetting.DEBUG, "Generating  JSInterop containers objects ...");
        List<JDefinedClass> toReturn = new ArrayList<>();
        for (Map.Entry<String, String> entry : packageModuleMap.entrySet()) {
            addPackageContainerObject(entry.getKey(), entry.getValue(), jCodeModel, topLevelElementsMap.get(entry.getKey()), toReturn/*, jsiNameClass, jsUtilsClass*/);
        }
        return toReturn;
    }

    protected static void addPackageContainerObject(String packageName, String containerObjectName, JCodeModel jCodeModel, final Map<String, JClass> topLevelElementsMap, List<JDefinedClass> toPopulate/*, JDefinedClass jsiNameClass, JDefinedClass jsUtilsClass*/) throws JClassAlreadyExistsException {
        log(LogLevelSetting.DEBUG, String.format("Looking for JSInterop container object %1$s for package %2$s ...", containerObjectName, packageName));
        toPopulate.add(getContainerObject(packageName, containerObjectName, jCodeModel, topLevelElementsMap/*, jsiNameClass, jsUtilsClass*/));
    }

    /**
     * @param packageName
     * @param containerObjectName
     * @param jCodeModel
     * @param topLevelElementsMap Map with the elementName (as found in xsd/xml) and the related <code>JClass</code>
     * @return
     * @throws JClassAlreadyExistsException
     */
    protected static JDefinedClass getContainerObject(String packageName, String containerObjectName, JCodeModel jCodeModel, final Map<String, JClass> topLevelElementsMap/*, JDefinedClass jsiNameClass, JDefinedClass jsUtilsClass*/) throws JClassAlreadyExistsException {
        log(LogLevelSetting.DEBUG, String.format("Creating  JSInterop container object %1$s for package %2$s ...", containerObjectName, packageName));
        final JDefinedClass toReturn = jCodeModel._class(packageName + "." + containerObjectName);
        toReturn.annotate(jCodeModel.ref(JsType.class)).param("isNative", true).param("namespace", jCodeModel.ref(JsPackage.class).staticRef("GLOBAL"));
        JDocComment comment = toReturn.javadoc();
        comment.append("JSInterop container for" + " " + "<code>" + packageName + "</code>");
        addNameProperty(jCodeModel, toReturn);
        for (Map.Entry<String, JClass> topLevelElementEntry : topLevelElementsMap.entrySet()) {
            addElementProperty(jCodeModel, toReturn, topLevelElementEntry.getKey(), topLevelElementEntry.getValue());
        }
//        addNewWrappedInstance(jCodeModel, toReturn, jsiNameClass, jsUtilsClass);
        return toReturn;
    }

//    protected static void addNewWrappedInstance(JCodeModel jCodeModel, JDefinedClass jDefinedClass, JDefinedClass jsiNameClass, JDefinedClass jsUtilsClass) {
//        log(LogLevelSetting.DEBUG, String.format("Add newWrappedInstance to object %1$s.%2$s ...", jDefinedClass._package().name(), jDefinedClass.name()));
//        String methodName = "newWrappedInstance";
//        int mod = JMod.PUBLIC + JMod.STATIC;
//        JMethod newMethod = jDefinedClass.method(mod, jDefinedClass, methodName);
//        String nameParameter = "nameParam";
//        final JVar nameParamVar = newMethod.param(jsiNameClass, nameParameter);
//        String valueParameter = "valueParam";
//        final JVar valueParamVar = newMethod.param(jCodeModel.ref(Object.class), valueParameter);
//        final JBlock body = newMethod.body();
//        body._return(jCodeModel.ref("jsinterop.base.Js").staticInvoke("uncheckedCast").arg(jsUtilsClass.staticInvoke("newWrappedInstance").arg(nameParamVar).arg(valueParamVar)));
//        JDocComment newComment = newMethod.javadoc();
//        String commentString = "Create <b>wrapped</b> instance of <code>" + jDefinedClass.name() + "</code>";
//        newComment.append(commentString);
//        newComment.addParam(nameParameter);
//        newComment.addParam(valueParameter);
//        newComment.addReturn();
//        newMethod.annotate(jCodeModel.ref(JsOverlay.class));
//    }

    protected static void addNameProperty(JCodeModel jCodeModel, JDefinedClass toPopulate) {
        log(LogLevelSetting.DEBUG, String.format("Add getName property to object %1$s.%2$s ...", toPopulate._package().name(), toPopulate.name()));
        JClass parameterRef = jCodeModel.ref(String.class);
        addGetter(jCodeModel, toPopulate, parameterRef, "Name", "name");
    }

    protected static void addElementProperty(JCodeModel jCodeModel, JDefinedClass toPopulate, String elementName, JClass elementClass) {
        log(LogLevelSetting.DEBUG, String.format("Add %1$s accessors to object %2$s.%3$s ...", elementName, toPopulate._package().name(), toPopulate.name()));
        String publicName = StringUtils.capitalize(elementName);
        addGetter(jCodeModel, toPopulate, elementClass, publicName, elementName);
        addSetter(jCodeModel, toPopulate, elementClass, publicName, elementName);
    }
}
