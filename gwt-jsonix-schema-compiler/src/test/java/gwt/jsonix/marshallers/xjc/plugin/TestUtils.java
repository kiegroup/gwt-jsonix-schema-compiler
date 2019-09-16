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

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.jvnet.jaxb2_commons.lang.StringUtils;

/**
 * Class to hold common static utilities
 */
public class TestUtils {

    /**
     * Returns a <code>JDefinedClass</code> in the given package with the given name. <b>If</b> outerClassName is provided, the generated
     * <code>JDefinedClass</code> would be an inner class of it
     * @param jCodeModel
     * @param packageName
     * @param className
     * @param outerClassName
     * @return
     * @throws JClassAlreadyExistsException
     */
    public static JDefinedClass getJDefinedClass(JCodeModel jCodeModel, String packageName, String className, String outerClassName) throws JClassAlreadyExistsException {
        if (!StringUtils.isEmpty(outerClassName)) {
            String fullBaseClassName = packageName + "." + outerClassName;
            JDefinedClass jDefinedBaseClass = jCodeModel._class(fullBaseClassName);
            return jDefinedBaseClass._class(className);
        } else {
            String fullClassName = packageName + "." + className;
           return jCodeModel._class(fullClassName);
        }
    }
}
