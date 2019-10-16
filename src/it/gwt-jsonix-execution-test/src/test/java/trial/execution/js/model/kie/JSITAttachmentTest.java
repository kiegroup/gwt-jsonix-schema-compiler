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

@SuppressWarnings("cast")
public class JSITAttachmentTest extends AbstractModelTest {

    public void testType() {
        assertEquals("KIE.TAttachment", JSITAttachment.TYPE);
    }

    public void testInstanceOf() {
        JSITAttachment retrieved = new JSITAttachment();
        assertNotNull(retrieved);
        assertTrue(JSITAttachment.instanceOf(retrieved));
    }

    public void testGetJSIName() {
        getJSIName(JSITAttachment.getJSIName(), "http://www.drools.org/kie/dmn/1.2", "tAttachment", "", "{http://www.drools.org/kie/dmn/1.2}", "{http://www.drools.org/kie/dmn/1.2}tAttachment");
    }

    public void testGetTYPE_NAME() {
        String retrieved = new JSITAttachment().getTYPE_NAME();
        assertNotNull(retrieved);
        assertEquals("KIE.TAttachment", retrieved);
    }

    public void testGetSetValue() {
        JSITAttachment jsitAttachment = new JSITAttachment();
        assertNull(jsitAttachment.getValue());
        jsitAttachment.setValue("valueParam");
        String retrieved = jsitAttachment.getValue();
        assertNotNull(retrieved);
        assertEquals("valueParam", retrieved);
    }

    public void testGetSetUrl() {
        JSITAttachment jsitAttachment = new JSITAttachment();
        assertNull(jsitAttachment.getUrl());
        jsitAttachment.setUrl("urlParam");
        String retrieved = jsitAttachment.getUrl();
        assertNotNull(retrieved);
        assertEquals("urlParam", retrieved);
    }

    public void testGetSetName() {
        JSITAttachment jsitAttachment = new JSITAttachment();
        assertNull(jsitAttachment.getName());
        jsitAttachment.setName("nameParam");
        String retrieved = jsitAttachment.getName();
        assertNotNull(retrieved);
        assertEquals("nameParam", retrieved);
    }
}