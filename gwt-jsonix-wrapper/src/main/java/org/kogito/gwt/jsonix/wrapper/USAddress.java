
package org.kogito.gwt.jsonix.wrapper;

import java.math.BigDecimal;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * JSInterop adapter for <code>USAddress</code>
 * 
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class USAddress {


    /**
     * Getter for <b>name</b>
     * 
     * @return
     *      <b>name</<b>
     */
    @JsProperty
    public final native String getName();

    /**
     * Setter for <b>name</b>
     * 
     * @param name
     *      <b>name</<b> to set.
     */
    @JsProperty
    public final native void setName(String name);

    /**
     * Getter for <b>piripicchio</b>
     * 
     * @return
     *      <b>piripicchio</<b>
     */
    @JsProperty
    public final native String getPiripicchio();

    /**
     * Setter for <b>piripicchio</b>
     * 
     * @param piripicchio
     *      <b>piripicchio</<b> to set.
     */
    @JsProperty
    public final native void setPiripicchio(String piripicchio);

    /**
     * Getter for <b>city</b>
     * 
     * @return
     *      <b>city</<b>
     */
    @JsProperty
    public final native String getCity();

    /**
     * Setter for <b>city</b>
     * 
     * @param city
     *      <b>city</<b> to set.
     */
    @JsProperty
    public final native void setCity(String city);

    /**
     * Getter for <b>state</b>
     * 
     * @return
     *      <b>state</<b>
     */
    @JsProperty
    public final native String getState();

    /**
     * Setter for <b>state</b>
     * 
     * @param state
     *      <b>state</<b> to set.
     */
    @JsProperty
    public final native void setState(String state);

    /**
     * Getter for <b>zip</b>
     * 
     * @return
     *      <b>zip</<b>
     */
    @JsProperty
    public final native BigDecimal getZip();

    /**
     * Setter for <b>zip</b>
     * 
     * @param zip
     *      <b>zip</<b> to set.
     */
    @JsProperty
    public final native void setZip(BigDecimal zip);

    /**
     * Getter for <b>country</b>
     * 
     * @return
     *      <b>country</<b>
     */
    @JsProperty
    public final native String getCountry();

    /**
     * Setter for <b>country</b>
     * 
     * @param country
     *      <b>country</<b> to set.
     */
    @JsProperty
    public final native void setCountry(String country);

}
