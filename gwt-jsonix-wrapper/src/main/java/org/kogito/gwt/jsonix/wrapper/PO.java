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
public class PO {

    /**
     * Gets the value of the shipTo property.
     * @return possible object is
     * {@link USAddress }
     */
    @JsProperty
    public final native USAddress getShipTo();

    /**
     * Sets the value of the shipTo property.
     * @param value allowed object is
     * {@link USAddress }
     */
    @JsProperty
    public final native void setShipTo(USAddress value);

    /**
     * Gets the value of the billTo property.
     * @return possible object is
     * {@link USAddress }
     */
    @JsProperty
    public final native USAddress getBillTo();

    /**
     * Sets the value of the billTo property.
     * @param value allowed object is
     * {@link USAddress }
     */
    @JsProperty
    public final native void setBillTo(USAddress value);

    /**
     * Gets the value of the comment property.
     * @return possible object is
     * {@link String }
     */
    @JsProperty
    public final native String getComment();

    /**
     * Sets the value of the comment property.
     * @param value allowed object is
     * {@link String }
     */
    @JsProperty
    public final native void setComment(String value);

    /**
     * Gets the value of the items property.
     * @return possible object is
     * {@link Items }
     */
    @JsProperty
    public final native Items getItems();

    /**
     * Sets the value of the items property.
     * @param value allowed object is
     * {@link Items }
     */
    @JsProperty
    public final native void setItems(Items value);

    //    /**
//     * Gets the value of the orderDate property.
//     *
//     * @return
//     *     possible object is
//     *     {@link XMLGregorianCalendar }
//     *
//     */
//     @JsProperty
 //   public final native XMLGregorianCalendar getOrderDate();

    //
//    /**
//     * Sets the value of the orderDate property.
//     *
//     * @param value
//     *     allowed object is
//     *     {@link XMLGregorianCalendar }
//     *
//     */
//     @JsProperty
   // public final native void setOrderDate(XMLGregorianCalendar value);
}
