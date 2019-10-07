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
package trial.execution.js.model.callbacks;

import java.lang.reflect.Method;

import jsinterop.annotations.JsFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommonCallbackTest {


    public static void testCallEvent(Class<?> toTest, Class<?> expectedParameter) {
        assertTrue(toTest.isInterface());
        assertEquals(1, toTest.getAnnotations().length);
        assertEquals(JsFunction.class, toTest.getAnnotations()[0].annotationType());
        assertEquals(1, toTest.getMethods().length);
        final Method callEventMethod = toTest.getMethods()[0];
        assertEquals("callEvent", callEventMethod.getName());
        assertEquals(Void.TYPE, callEventMethod.getReturnType());
        assertEquals(1, callEventMethod.getParameterCount());
        assertEquals(expectedParameter, callEventMethod.getParameterTypes()[0]);
    }
}
