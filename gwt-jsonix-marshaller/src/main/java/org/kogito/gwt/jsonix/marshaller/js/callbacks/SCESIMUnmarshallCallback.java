
package org.kogito.gwt.jsonix.marshaller.js.callbacks;

import jsinterop.annotations.JsFunction;
import org.kogito.gwt.jsonix.marshaller.js.model.SCESIM;


/**
 * Unmarshaller callback for <code>SCESIM</code>
 * 
 */
@JsFunction
public interface SCESIMUnmarshallCallback {


    void callEvent(SCESIM sCESIM);

}
