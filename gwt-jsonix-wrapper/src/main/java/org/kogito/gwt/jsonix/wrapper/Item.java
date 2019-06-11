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
public class Item {

    /**
     * Gets the value of the productName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
     @JsProperty
    public  final native String getProductName();

    /**
     * Sets the value of the productName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
     @JsProperty
    public  final native void setProductName(String value);

    /**
     * Gets the value of the quantity property.
     *
     */
     @JsProperty
    public  final native int getQuantity();

    /**
     * Sets the value of the quantity property.
     *
     */
     @JsProperty
    public  final native void setQuantity(int value);

//    /**
//     * Gets the value of the usPrice property.
//     *
//     * @return
//     *     possible object is
//     *     {@link BigDecimal }
//     *
//     */
//    public BigDecimal getUSPrice() {
//        return usPrice;
//    }
//
//    /**
//     * Sets the value of the usPrice property.
//     *
//     * @param value
//     *     allowed object is
//     *     {@link BigDecimal }
//     *
//     */
//    public void setUSPrice(BigDecimal value) {
//        this.usPrice = value;
//    }

    /**
     * Gets the value of the comment property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
     @JsProperty
    public  final native String getComment();

    /**
     * Sets the value of the comment property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
     @JsProperty
    public  final native void setComment(String value);

//    /**
//     * Gets the value of the shipDate property.
//     *
//     * @return
//     *     possible object is
//     *     {@link XMLGregorianCalendar }
//     *
//     */
//    public XMLGregorianCalendar getShipDate() {
//        return shipDate;
//    }
//
//    /**
//     * Sets the value of the shipDate property.
//     *
//     * @param value
//     *     allowed object is
//     *     {@link XMLGregorianCalendar }
//     *
//     */
//    public void setShipDate(XMLGregorianCalendar value) {
//        this.shipDate = value;
//    }

    /**
     * Gets the value of the partNum property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
     @JsProperty
    public  final native String getPartNum();

    /**
     * Sets the value of the partNum property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
     @JsProperty
    public  final native void setPartNum(String value);

}
