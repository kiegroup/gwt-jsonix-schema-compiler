
package org.kogito.gwt.jsonix.marshaller.js.model;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;


/**
 * JSInterop adapter for <code>ExpressionElementsType</code>
 * 
 */
@JsType(namespace = JsPackage.GLOBAL, name = "ExpressionElementsType")
public class JSIExpressionElementsType {


    /**
     * Getter for <b>expressionElement</b>
     * 
     * @return
     *      <b>expressionElement</<b>
     */
    @JsProperty(name = "expressionElement")
    public final native JSIExpressionElementType getExpressionElement();

    /**
     * Setter for <b>expressionElement</b>
     * 
     * @param expressionElement
     *      <b>expressionElement</<b> to set.
     */
    @JsProperty(name = "expressionElement")
    public final native void setExpressionElement(JSIExpressionElementType expressionElement);
    



public static native JSIExpressionElementsType newInstance() /*-{
        var json = "{\"TYPE_NAME\": \"SCESIM.ExpressionElementsType\"}";
        var retrieved = JSON.parse(json)
        return retrieved
    }-*/;
}
