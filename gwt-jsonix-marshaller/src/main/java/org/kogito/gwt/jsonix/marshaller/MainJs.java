
package org.kogito.gwt.jsonix.marshaller;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.kogito.gwt.jsonix.marshaller.callbacks.POMarshallCallback;
import org.kogito.gwt.jsonix.marshaller.callbacks.POUnmarshallCallback;
import org.kogito.gwt.jsonix.marshaller.po.PO;


/**
 * JSInterop adapter to use for marshalling/unmarshalling.
 * 
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class MainJs {


    @JsMethod
    public final static native void unmarshall(String xmlString, POUnmarshallCallback pOUnmarshallCallback);

    @JsMethod
    public final static native void marshall(PO pO, POMarshallCallback pOMarshallCallback);

}
