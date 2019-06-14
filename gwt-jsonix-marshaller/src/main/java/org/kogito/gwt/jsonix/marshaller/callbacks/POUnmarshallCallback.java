
package org.kogito.gwt.jsonix.marshaller.callbacks;

import jsinterop.annotations.JsFunction;
import org.kogito.gwt.jsonix.marshaller.po.PO;


/**
 * Unmarshaller callback for <code>PO</code>
 * 
 */
@JsFunction
public interface POUnmarshallCallback {


    void callEvent(PO pO);

}
