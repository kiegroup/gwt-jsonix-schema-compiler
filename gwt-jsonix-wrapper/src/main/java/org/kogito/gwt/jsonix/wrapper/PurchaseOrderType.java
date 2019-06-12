
package org.kogito.gwt.jsonix.wrapper;

import java.util.Date;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * JSInterop adapter for <code>PurchaseOrderType</code>
 * 
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class PurchaseOrderType {


    /**
     * Getter for <b>shipTo</b>
     * 
     * @return
     *      <b>shipTo</<b>
     */
    @JsProperty
    public final native USAddress getShipTo();

    /**
     * Setter for <b>shipTo</b>
     * 
     * @param shipTo
     *      <b>shipTo</<b> to set.
     */
    @JsProperty
    public final native void setShipTo(USAddress shipTo);

    /**
     * Getter for <b>billTo</b>
     * 
     * @return
     *      <b>billTo</<b>
     */
    @JsProperty
    public final native USAddress getBillTo();

    /**
     * Setter for <b>billTo</b>
     * 
     * @param billTo
     *      <b>billTo</<b> to set.
     */
    @JsProperty
    public final native void setBillTo(USAddress billTo);

    /**
     * Getter for <b>comment</b>
     * 
     * @return
     *      <b>comment</<b>
     */
    @JsProperty
    public final native String getComment();

    /**
     * Setter for <b>comment</b>
     * 
     * @param comment
     *      <b>comment</<b> to set.
     */
    @JsProperty
    public final native void setComment(String comment);

    /**
     * Getter for <b>items</b>
     * 
     * @return
     *      <b>items</<b>
     */
    @JsProperty
    public final native Items getItems();

    /**
     * Setter for <b>items</b>
     * 
     * @param items
     *      <b>items</<b> to set.
     */
    @JsProperty
    public final native void setItems(Items items);

    /**
     * Getter for <b>orderDate</b>
     * 
     * @return
     *      <b>orderDate</<b>
     */
    @JsProperty
    public final native Date getOrderDate();

    /**
     * Setter for <b>orderDate</b>
     * 
     * @param orderDate
     *      <b>orderDate</<b> to set.
     */
    @JsProperty
    public final native void setOrderDate(Date orderDate);

}
