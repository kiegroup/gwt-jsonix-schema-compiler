/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gwt.jsonix.marshallers.xjc.plugin.builders;

public class ClassNameHelper {

    private static final String PREFIX = "JsInterop__ConstructorAPI__DMN";

    private static final String SEPARATOR = "__";

    private static final String CLASS_PREFIX = "JSI";

    public static String getJsInteropTypeName(final String className) {
        return join(PREFIX, CLASS_PREFIX + className);
    }

    public static String getJsInteropTypeName(final String moduleName,
                                              final String className) {
        return join(PREFIX, moduleName, CLASS_PREFIX + className);
    }

    private static String join(final String... nameParts) {
        return String.join(SEPARATOR, nameParts);
    }
}
