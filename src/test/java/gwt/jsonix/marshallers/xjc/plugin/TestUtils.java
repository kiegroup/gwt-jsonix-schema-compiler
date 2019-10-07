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

import java.util.Collection;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.api.ClassNameAllocator;
import com.sun.tools.xjc.model.Model;
import com.sun.xml.bind.api.impl.NameConverter;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;
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

    public static Model getModel() {
        return new Model(new Options(), new JCodeModel(), new NameConverter.Standard(), getClassNameAllocator(), getXSSchemaSet());
    }

    private static ClassNameAllocator getClassNameAllocator() {
        return (packageName, className) -> null;
    }

    private static XSSchemaSet getXSSchemaSet() {
        return new XSSchemaSet() {
            @Override
            public XSSchema getSchema(String s) {
                return null;
            }

            @Override
            public XSSchema getSchema(int i) {
                return null;
            }

            @Override
            public int getSchemaSize() {
                return 0;
            }

            @Override
            public Iterator<XSSchema> iterateSchema() {
                return null;
            }

            @Override
            public Collection<XSSchema> getSchemas() {
                return null;
            }

            @Override
            public XSType getType(String s, String s1) {
                return null;
            }

            @Override
            public XSSimpleType getSimpleType(String s, String s1) {
                return null;
            }

            @Override
            public XSAttributeDecl getAttributeDecl(String s, String s1) {
                return null;
            }

            @Override
            public XSElementDecl getElementDecl(String s, String s1) {
                return null;
            }

            @Override
            public XSModelGroupDecl getModelGroupDecl(String s, String s1) {
                return null;
            }

            @Override
            public XSAttGroupDecl getAttGroupDecl(String s, String s1) {
                return null;
            }

            @Override
            public XSComplexType getComplexType(String s, String s1) {
                return null;
            }

            @Override
            public XSIdentityConstraint getIdentityConstraint(String s, String s1) {
                return null;
            }

            @Override
            public Iterator<XSElementDecl> iterateElementDecls() {
                return null;
            }

            @Override
            public Iterator<XSType> iterateTypes() {
                return null;
            }

            @Override
            public Iterator<XSAttributeDecl> iterateAttributeDecls() {
                return null;
            }

            @Override
            public Iterator<XSAttGroupDecl> iterateAttGroupDecls() {
                return null;
            }

            @Override
            public Iterator<XSModelGroupDecl> iterateModelGroupDecls() {
                return null;
            }

            @Override
            public Iterator<XSSimpleType> iterateSimpleTypes() {
                return null;
            }

            @Override
            public Iterator<XSComplexType> iterateComplexTypes() {
                return null;
            }

            @Override
            public Iterator<XSNotation> iterateNotations() {
                return null;
            }

            @Override
            public Iterator<XSIdentityConstraint> iterateIdentityConstraints() {
                return null;
            }

            @Override
            public XSComplexType getAnyType() {
                return null;
            }

            @Override
            public XSSimpleType getAnySimpleType() {
                return null;
            }

            @Override
            public XSContentType getEmpty() {
                return null;
            }

            @Override
            public Collection<XSComponent> select(String s, NamespaceContext namespaceContext) {
                return null;
            }

            @Override
            public XSComponent selectSingle(String s, NamespaceContext namespaceContext) {
                return null;
            }
        };
    }
}
