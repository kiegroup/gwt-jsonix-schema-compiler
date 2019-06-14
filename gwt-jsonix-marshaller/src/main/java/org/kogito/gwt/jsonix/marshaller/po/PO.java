
package org.kogito.gwt.jsonix.marshaller.po;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;


/**
 * JSInterop container for <code>JSIPurchaseOrderType</code>
 * 
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class PO {


    @JsProperty
    public final native String getName();

    @JsProperty
    public final native JSIPurchaseOrderType getValue();

}
