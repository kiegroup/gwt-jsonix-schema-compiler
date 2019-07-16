
package org.kogito.gwt.jsonix.marshaller.js.model;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;


/**
 * JSInterop adapter for <code>ScenariosType</code>
 * 
 */
@JsType(namespace = JsPackage.GLOBAL, name = "ScenariosType")
public class JSIScenariosType {


    /**
     * Getter for <b>scenario</b>
     * 
     * @return
     *      <b>scenario</<b>
     */
    @JsProperty(name = "scenario")
    public final native JSIScenarioType getScenario();

    /**
     * Setter for <b>scenario</b>
     * 
     * @param scenario
     *      <b>scenario</<b> to set.
     */
    @JsProperty(name = "scenario")
    public final native void setScenario(JSIScenarioType scenario);
    



public static native JSIScenariosType newInstance() /*-{
        var json = "{\"TYPE_NAME\": \"SCESIM.ScenariosType\"}";
        var retrieved = JSON.parse(json)
        return retrieved
    }-*/;
}
