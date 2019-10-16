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

package trial.execution.js.model.kie;

import java.util.Arrays;
import java.util.List;

import jsinterop.base.JsArrayLike;
import trial.execution.mapper.JsUtils;

@SuppressWarnings("cast")
public class JSITComponentsWidthsExtensionTest extends AbstractModelTest {

    public void testType() {
        assertEquals("KIE.TComponentsWidthsExtension", JSITComponentsWidthsExtension.TYPE);
    }

    public void testInstanceOf() {
        JSITComponentsWidthsExtension retrieved = new JSITComponentsWidthsExtension();
        assertNotNull(retrieved);
        assertTrue(JSITComponentsWidthsExtension.instanceOf(retrieved));
    }

    public void testGetJSIName() {
        getJSIName(JSITComponentsWidthsExtension.getJSIName(), "http://www.drools.org/kie/dmn/1.2", "tComponentsWidthsExtension", "", "{http://www.drools.org/kie/dmn/1.2}", "{http://www.drools.org/kie/dmn/1.2}tComponentsWidthsExtension");
    }

    public void testGetTYPE_NAME() {
        String retrieved = new JSITComponentsWidthsExtension().getTYPE_NAME();
        assertNotNull(retrieved);
        assertEquals("KIE.TComponentsWidthsExtension", retrieved);
    }

    public void testGetComponentWidths() {
        JSITComponentsWidthsExtension jsitComponentWidths = new JSITComponentsWidthsExtension();
        final List<JSITComponentWidths> retrieved = jsitComponentWidths.getComponentWidths();
        assertNotNull(retrieved);
        assertTrue(retrieved.isEmpty());
    }

    public void testAddComponentWidths() {
        JSITComponentsWidthsExtension jsitComponentWidths = new JSITComponentsWidthsExtension();
        JSITComponentWidths toAdd = new JSITComponentWidths();
        jsitComponentWidths.addComponentWidths(toAdd);
        final List<JSITComponentWidths> retrieved = jsitComponentWidths.getComponentWidths();
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
        assertEquals(toAdd, retrieved.get(0));
    }

    public void testAddAllComponentWidths() {
        JSITComponentsWidthsExtension jsitComponentWidths = new JSITComponentsWidthsExtension();
        JSITComponentWidths[] toAdd = {new JSITComponentWidths(), new JSITComponentWidths(), new JSITComponentWidths()};
        jsitComponentWidths.addAllComponentWidths(toAdd);
        final List<JSITComponentWidths> retrieved = jsitComponentWidths.getComponentWidths();
        assertNotNull(retrieved);
        assertEquals(toAdd.length, retrieved.size());
        for (int i = 0; i < toAdd.length; i++) {
            assertEquals(toAdd[i], retrieved.get(i));
        }
    }

    public void testRemovComponentWidths() {
        JSITComponentsWidthsExtension jsitComponentWidths = new JSITComponentsWidthsExtension();
        JSITComponentWidths[] toAdd = {new JSITComponentWidths(), new JSITComponentWidths(), new JSITComponentWidths()};
        jsitComponentWidths.addAllComponentWidths(toAdd);
        jsitComponentWidths.removeComponentWidths(1);
        final List<JSITComponentWidths> retrieved = jsitComponentWidths.getComponentWidths();
        assertNotNull(retrieved);
        assertEquals(2, retrieved.size());
        assertEquals(toAdd[0], retrieved.get(0));
        assertEquals(toAdd[2], retrieved.get(1));
    }

    public void testGetSetNativeComponentWidths() {
        JSITComponentsWidthsExtension jsitComponentWidths = new JSITComponentsWidthsExtension();
        assertNull(jsitComponentWidths.getNativeComponentWidths());
        JsArrayLike<JSITComponentWidths> toAdd = JsUtils.toJsArrayLike(Arrays.asList(new JSITComponentWidths(), new JSITComponentWidths(), new JSITComponentWidths()));
        jsitComponentWidths.setNativeComponentWidths(toAdd);
        assertEquals(toAdd, jsitComponentWidths.getNativeComponentWidths());
    }

    public void testSetComponentWidths() {
        JSITComponentsWidthsExtension jsitComponentWidths = new JSITComponentsWidthsExtension();
        List<JSITComponentWidths> toSet = Arrays.asList(new JSITComponentWidths(), new JSITComponentWidths(), new JSITComponentWidths());
        jsitComponentWidths.setComponentWidths(toSet);
        JsArrayLike<JSITComponentWidths> retrieved = jsitComponentWidths.getNativeComponentWidths();
        assertEquals(toSet.size(), retrieved.getLength());
        for (int i = 0; i < toSet.size(); i++) {
            assertEquals(toSet.get(i), retrieved.getAt(i));
        }
    }

}