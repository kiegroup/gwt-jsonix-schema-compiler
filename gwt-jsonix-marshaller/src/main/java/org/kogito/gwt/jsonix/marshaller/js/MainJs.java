
package org.kogito.gwt.jsonix.marshaller.js;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.kogito.gwt.jsonix.marshaller.js.callbacks.SCESIMMarshallCallback;
import org.kogito.gwt.jsonix.marshaller.js.callbacks.SCESIMUnmarshallCallback;
import org.kogito.gwt.jsonix.marshaller.js.model.SCESIM;


/**
 * JSInterop adapter to use for marshalling/unmarshalling.
 * 
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class MainJs {


    @JsMethod
    public final static native void unmarshall(String xmlString, SCESIMUnmarshallCallback sCESIMUnmarshallCallback);

    @JsMethod
    public final static native void marshall(SCESIM sCESIM, SCESIMMarshallCallback sCESIMMarshallCallback);

}
