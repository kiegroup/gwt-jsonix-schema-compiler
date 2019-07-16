
package org.kogito.gwt.jsonix.marshaller.js.model;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsArrayLike;


/**
 * JSInterop adapter for <code>InnerImportsType</code>
 * 
 */
@JsType(namespace = JsPackage.GLOBAL, name = "InnerImportsType")
public class JSIInnerImportsType {


    /**
     * Getter for <b>_import</b>
     * 
     * @return
     *      <b>_import</<b>
     */
    @JsProperty(name = "_import")
    public final native JsArrayLike<JSIImportType> getImport();

    /**
     * Setter for <b>_import</b>
     * 
     * @param _import
     *      <b>_import</<b> to set.
     */
    @JsProperty(name = "_import")
    public final native void setImport(JsArrayLike<JSIImportType> _import);
    



public static native JSIInnerImportsType newInstance() /*-{
        var json = "{\"TYPE_NAME\": \"SCESIM.InnerImportsType\"}";
        var retrieved = JSON.parse(json)
        return retrieved
    }-*/;
}
