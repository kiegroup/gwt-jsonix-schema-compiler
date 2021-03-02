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

@SuppressWarnings("cast")
public class JSITComponentsWidthsTest extends AbstractModelTest {

    public void testType() {
        assertEquals("KIE.TComponentWidths", JSITComponentWidths.TYPE);
    }

    public void testInstanceOf() {
        JSITComponentWidths retrieved = JSITComponentWidths.newInstance();
        assertNotNull(retrieved);
        assertTrue(JSITComponentWidths.instanceOf(retrieved));
    }

    public void testGetJSIName() {
        getJSIName(JSITComponentWidths.getJSIName(), "http://www.drools.org/kie/dmn/1.2", "tComponentWidths", "", "{http://www.drools.org/kie/dmn/1.2}", "{http://www.drools.org/kie/dmn/1.2}tComponentWidths");
    }

    public void testGetTYPE_NAME() {
        String retrieved = JSITComponentWidths.newInstance().getTYPE_NAME();
        assertNotNull(retrieved);
        assertEquals("KIE.TComponentWidths", retrieved);
    }

    public void testGetWidth() {
        JSITComponentWidths jsitComponentWidths = JSITComponentWidths.newInstance();
        assertNotNull(jsitComponentWidths.getWidth());
        assertTrue(jsitComponentWidths.getWidth().isEmpty());
    }

    public void testAddWidth() {
        JSITComponentWidths jsitComponentWidths = JSITComponentWidths.newInstance();
        jsitComponentWidths.addWidth(4.5F);
        final List<Float> retrieved = jsitComponentWidths.getWidth();
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
        assertEquals(4.5F, retrieved.get(0));
    }

    public void testAddAllWidth() {
        JSITComponentWidths jsitComponentWidths = JSITComponentWidths.newInstance();
        float[] toAdd = {3.2F, 7.42F, 1.214F};
        jsitComponentWidths.addAllWidth(toAdd);
        final List<Float> retrieved = jsitComponentWidths.getWidth();
        assertNotNull(retrieved);
        assertEquals(toAdd.length, retrieved.size());
        for (int i = 0; i < toAdd.length; i++) {
            assertEquals(toAdd[i], retrieved.get(i));
        }
    }

    public void testRemoveWidth() {
        JSITComponentWidths jsitComponentWidths = JSITComponentWidths.newInstance();
        float[] toAdd = {3.2F, 7.42F, 1.214F};
        jsitComponentWidths.addAllWidth(toAdd);
        jsitComponentWidths.removeWidth(1);
        final List<Float> retrieved = jsitComponentWidths.getWidth();
        assertNotNull(retrieved);
        assertEquals(2, retrieved.size());
        assertEquals(toAdd[0], retrieved.get(0));
        assertEquals(toAdd[2], retrieved.get(1));
    }

    public void testGetSetNativeWidth() {
        JSITComponentWidths jsitComponentWidths = JSITComponentWidths.newInstance();
        assertNull(jsitComponentWidths.getNativeWidth());
        float[] toAdd = {3.2F, 7.42F, 1.214F};
        jsitComponentWidths.setNativeWidth(toAdd);
        assertEquals(toAdd, jsitComponentWidths.getNativeWidth());
    }

    public void testSetWidth() {
        JSITComponentWidths jsitComponentWidths = JSITComponentWidths.newInstance();
        List<Float> toSet = Arrays.asList(3.2F, 7.42F, 1.214F);
        jsitComponentWidths.setWidth(toSet);
        final float[] retrieved = jsitComponentWidths.getNativeWidth();
        assertEquals(toSet.size(), retrieved.length);
        for (int i = 0; i < toSet.size(); i++) {
            assertEquals(toSet.get(i), retrieved[i]);
        }
    }

    public void testGetSetDmnElementRef() {
        JSITComponentWidths jsitComponentWidths = JSITComponentWidths.newInstance();
        assertNull(jsitComponentWidths.getDmnElementRef());
        jsitComponentWidths.setDmnElementRef("dmnElementRefParam");
        String retrieved = jsitComponentWidths.getDmnElementRef();
        assertNotNull(retrieved);
        assertEquals("dmnElementRefParam", retrieved);
    }
}