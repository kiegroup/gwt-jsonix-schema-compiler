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

package trial.execution.mapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.xml.namespace.QName;

import jsinterop.base.JsArrayLike;

public class JsUtilsTest extends AbstractGWTTestCase {

    private final String[] ARRAY_TO_ADD = {"TO_ADD_1", "TO_ADD_2", "TO_ADD_3"};

    public void testAdd() {
        JsArrayLike<String> jsArrayLike = JsUtils.getNativeArray();
        assertEquals(0, jsArrayLike.getLength());
        String toAdd = "TO_ADD";
        JsUtils.add(jsArrayLike, toAdd);
        assertEquals(1, jsArrayLike.getLength());
        assertEquals(toAdd, jsArrayLike.getAt(0));
    }

    public void testAddAll() {
        JsArrayLike<String> jsArrayLike = getPopulatedJsArrayLike();
        assertEquals(ARRAY_TO_ADD.length, jsArrayLike.getLength());
        for (int i = 0; i < ARRAY_TO_ADD.length; i++) {
            assertEquals(ARRAY_TO_ADD[i], jsArrayLike.getAt(i));
        }
    }

    public void testRemove() {
        JsArrayLike<String> jsArrayLike = getPopulatedJsArrayLike();
        JsUtils.remove(jsArrayLike, 1);
        assertEquals(ARRAY_TO_ADD.length - 1, jsArrayLike.getLength());
        assertEquals(ARRAY_TO_ADD[0], jsArrayLike.getAt(0));
        assertEquals(ARRAY_TO_ADD[2], jsArrayLike.getAt(1));
    }

    public void testToList() {
        JsArrayLike<String> jsArrayLike = getPopulatedJsArrayLike();
        final List<String> retrieved = JsUtils.toList(jsArrayLike);
        assertNotNull(retrieved);
        assertEquals(jsArrayLike.getLength(), retrieved.size());
        for (int i = 0; i < jsArrayLike.getLength(); i++) {
            assertEquals(jsArrayLike.getAt(i), retrieved.get(i));
        }
    }

    public void testToJsArrayLike() {
        List<String> list = getPopulatedList();
        JsArrayLike<String> retrieved = JsUtils.toJsArrayLike(list);
        assertNotNull(retrieved);
        assertEquals(list.size(), retrieved.getLength());
        for (int i = 0; i < list.size(); i++) {
            assertEquals(list.get(i), retrieved.getAt(i));
        }
    }

    public void testToAttributesMap() {
        Map<QName, String> object = getObjectToMap();
        final Map<QName, String> retrieved = JsUtils.toAttributesMap(object);
        assertNotNull(retrieved);
        // TODO {gcardosi} finish this test
    }

    public void testToAttributesMapNull() {
        Map<QName, String> object = null;
        final Map<QName, String> retrieved = JsUtils.toAttributesMap(object);
        assertNotNull(retrieved);
        assertEquals(0, retrieved.size());
    }

    public void testFromAttributesMap() {
        // TODO (?) {gcardosi}
    }

    public void testNewWrappedInstance() {
        Object retrieved = JsUtils.newWrappedInstance();
        assertNotNull(retrieved);
    }

    public void testSetNameOnWrapped() {
        // TODO (?) {gcardosi}
    }

    public void testSetValueOnWrapped() {
        // TODO (?) {gcardosi}
    }

    public void testGetNativeElementsArray() {
        JsArrayLike<String> retrieved = JsUtils.getNativeElementsArray(null);
        assertNotNull(retrieved);
        assertEquals(0, retrieved.getLength());
        final JsArrayLike<String> populatedJsArrayLike = getPopulatedJsArrayLike();
        retrieved = JsUtils.getNativeElementsArray(populatedJsArrayLike);
        assertNotNull(retrieved);
        assertEquals(populatedJsArrayLike, retrieved);
    }

    public void testGetUnwrappedElementsArray() {
        // TODO (?) {gcardosi}
    }

    public void testGetUnwrappedElement() {
        // TODO (?) {gcardosi}
    }

    public void testGetWrappedElement() {
        // TODO (?) {gcardosi}
    }

    public void testGetNativeArray() {
        JsArrayLike<String> retrieved = JsUtils.getNativeArray();
        assertNotNull(retrieved);
        assertEquals(0, retrieved.getLength());
    }

    public void testGetTypeName() {
        // TODO (?) {gcardosi}
    }

    public void testGetJSIName() {
        final JSIName retrieved = JsUtils.getJSIName("namespace", "localpart", "prefix");
        assertNotNull(retrieved);
        assertEquals("namespace", retrieved.getNamespaceURI());
        assertEquals("localpart", retrieved.getLocalPart());
        assertEquals("prefix", retrieved.getPrefix());
        assertEquals("{namespace}localpart", retrieved.getKey());
        assertEquals("{namespace}prefix:localpart", retrieved.getString());
    }

    private JsArrayLike<String> getPopulatedJsArrayLike() {
        JsArrayLike<String> toReturn = JsUtils.getNativeArray();
        assertEquals(0, toReturn.getLength());
        JsUtils.addAll(toReturn, ARRAY_TO_ADD);
        return toReturn;
    }

    private List<String> getPopulatedList() {
        return Arrays.asList(ARRAY_TO_ADD);
    }

    private Map<QName, String> getObjectToMap() {
        Map<QName, String> toReturn = new HashMap<>();
        IntStream.range(0, 2).forEach(index -> {
            QName key = new QName("http://namespace-" + index, "localPart-" + index, "prefix" + index);
            toReturn.put(key, "FIELD-" + index);
        });
        return toReturn;
    }

    private class TestObject {

        public String TYPE_NAME = "TEST_OBJECT";

    }
}