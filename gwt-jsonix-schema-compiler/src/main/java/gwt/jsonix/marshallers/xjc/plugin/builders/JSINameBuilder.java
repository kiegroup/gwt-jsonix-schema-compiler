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

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpression;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.apache.commons.lang3.StringUtils;
import org.hisrc.jsonix.settings.LogLevelSetting;

import static gwt.jsonix.marshallers.xjc.plugin.builders.BuilderUtils.addNativeGetter;
import static gwt.jsonix.marshallers.xjc.plugin.builders.BuilderUtils.addSetter;
import static gwt.jsonix.marshallers.xjc.plugin.builders.BuilderUtils.log;
import static gwt.jsonix.marshallers.xjc.plugin.builders.ClassNameHelper.getJsInteropTypeName;

/**
 * Build the <b>JSIName</b> class representing the <b>name</b> attribute of a <b>wrapped</b> object
 */
public class JSINameBuilder {

    private static final String[] FIELDS = {"namespaceURI", "localPart", "prefix", "key", "string"};

    private JSINameBuilder() {
    }

    public static JDefinedClass generateJSINameClass(JCodeModel jCodeModel, String jsMainPackage) throws JClassAlreadyExistsException {
        final JDefinedClass toReturn = getJSINameClass(jCodeModel, jsMainPackage);
        populateJSINameClass(jCodeModel, toReturn);
        return toReturn;
    }

    protected static void populateJSINameClass(JCodeModel jCodeModel, JDefinedClass jDefinedClass) {
        for (String field : FIELDS) {
            addField(jCodeModel, jDefinedClass, field);
        }
    }

    protected static void addField(JCodeModel jCodeModel, JDefinedClass jDefinedClass, String field) {
        addNativeGetter(jCodeModel, jDefinedClass, jCodeModel.ref(String.class), StringUtils.capitalize(field), field);
        addSetter(jCodeModel, jDefinedClass, jCodeModel.ref(String.class), StringUtils.capitalize(field), field);
    }

    protected static JDefinedClass getJSINameClass(JCodeModel jCodeModel, String jsMainPackage) throws
            JClassAlreadyExistsException {

        log(LogLevelSetting.DEBUG, "Creating JSIName class");

        final String className = "Name";

        if (!jsMainPackage.isEmpty() && !jsMainPackage.endsWith(".")) {
            jsMainPackage += ".";
        }

        final JExpression nameSpaceExpression = jCodeModel.ref(JsPackage.class).staticRef("GLOBAL");
        final JDefinedClass toReturn = jCodeModel._class(jsMainPackage + "JSI" + className);
        final String jsTypeName = getJsInteropTypeName(className);

        toReturn.annotate(jCodeModel.ref(JsType.class))
                .param("namespace", nameSpaceExpression)
                .param("name", jsTypeName)
                .param("isNative", true);

        JDocComment comment = toReturn.javadoc();
        comment.append("Class representing the <b>name</b> attribute of a <b>wrapped</b> object");
        return toReturn;
    }
}
