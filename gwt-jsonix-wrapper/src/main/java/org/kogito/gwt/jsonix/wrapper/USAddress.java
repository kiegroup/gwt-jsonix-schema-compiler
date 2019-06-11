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
package org.kogito.gwt.jsonix.wrapper;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class USAddress {

    /**
     * Gets the value of the name property.
     * @return possible object is
     * {@link String }
     */
     @JsProperty
    public  final native String getName();

    /**
     * Sets the value of the name property.
     * @param value allowed object is
     * {@link String }
     */
     @JsProperty
    public  final native void setName(String value);

    /**
     * Gets the value of the piripicchio property.
     * @return possible object is
     * {@link String }
     */
     @JsProperty
    public  final native String getPiripicchio();

    /**
     * Sets the value of the piripicchio property.
     * @param value allowed object is
     * {@link String }
     */
     @JsProperty
    public  final native void setPiripicchio(String value);

    /**
     * Gets the value of the city property.
     * @return possible object is
     * {@link String }
     */
     @JsProperty
    public  final native String getCity();

    /**
     * Sets the value of the city property.
     * @param value allowed object is
     * {@link String }
     */
     @JsProperty
    public  final native void setCity(String value);

    /**
     * Gets the value of the state property.
     * @return possible object is
     * {@link String }
     */
     @JsProperty
    public  final native String getState();

    /**
     * Sets the value of the state property.
     * @param value allowed object is
     * {@link String }
     */
     @JsProperty
    public  final native void setState(String value);

//    /**
//     * Gets the value of the zip property.
//     *
//     * @return
//     *     possible object is
//     *     {@link BigDecimal }
//     *
//     */
//    public final native BigDecimal getZip() {
//        return zip;
//    }
//
//    /**
//     * Sets the value of the zip property.
//     *
//     * @param value
//     *     allowed object is
//     *     {@link BigDecimal }
//     *
//     */
//    public final native void setZip(BigDecimal value) {
//        this.zip = value;
//    }

    /**
     * Gets the value of the country property.
     * @return possible object is
     * {@link String }
     */
     @JsProperty
    public  final native String getCountry();

    /**
     * Sets the value of the country property.
     * @param value allowed object is
     * {@link String }
     */
     @JsProperty
    public  final native void setCountry(String value);
}
