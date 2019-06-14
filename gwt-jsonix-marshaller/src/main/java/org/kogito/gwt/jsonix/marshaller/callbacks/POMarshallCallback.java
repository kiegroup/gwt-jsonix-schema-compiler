
package org.kogito.gwt.jsonix.marshaller.callbacks;

import jsinterop.annotations.JsFunction;


/**
 * Marshaller callback for <code>PO</code>
 * 
 */
@JsFunction
public interface POMarshallCallback {


    void callEvent(String xmlString);

}
