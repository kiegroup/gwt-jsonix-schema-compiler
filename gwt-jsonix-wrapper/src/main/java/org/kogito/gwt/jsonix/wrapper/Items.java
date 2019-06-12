
package org.kogito.gwt.jsonix.wrapper;

import java.math.BigDecimal;
import java.util.Date;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * JSInterop adapter for <code>Items</code>
 * 
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class Items {


    /**
     * Getter for <b>item</b>
     * 
     * @return
     *      <b>item</<b>
     */
    @JsProperty
    public final native Item[] getItem();

    /**
     * Setter for <b>item</b>
     * 
     * @param item
     *      <b>item</<b> to set.
     */
    @JsProperty
    public final native void setItem(Item[] item);


    /**
     * JSInterop adapter for <code>Item</code>
     * 
     */
    @JsType(isNative = true, namespace = JsPackage.GLOBAL)
    public static class Item {


        /**
         * Getter for <b>productName</b>
         * 
         * @return
         *      <b>productName</<b>
         */
        @JsProperty
        public final native String getProductName();

        /**
         * Setter for <b>productName</b>
         * 
         * @param productName
         *      <b>productName</<b> to set.
         */
        @JsProperty
        public final native void setProductName(String productName);

        /**
         * Getter for <b>quantity</b>
         * 
         * @return
         *      <b>quantity</<b>
         */
        @JsProperty
        public final native Integer getQuantity();

        /**
         * Setter for <b>quantity</b>
         * 
         * @param quantity
         *      <b>quantity</<b> to set.
         */
        @JsProperty
        public final native void setQuantity(Integer quantity);

        /**
         * Getter for <b>usPrice</b>
         * 
         * @return
         *      <b>usPrice</<b>
         */
        @JsProperty
        public final native BigDecimal getUSPrice();

        /**
         * Setter for <b>usPrice</b>
         * 
         * @param usPrice
         *      <b>usPrice</<b> to set.
         */
        @JsProperty
        public final native void setUSPrice(BigDecimal usPrice);

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
         * Getter for <b>shipDate</b>
         * 
         * @return
         *      <b>shipDate</<b>
         */
        @JsProperty
        public final native Date getShipDate();

        /**
         * Setter for <b>shipDate</b>
         * 
         * @param shipDate
         *      <b>shipDate</<b> to set.
         */
        @JsProperty
        public final native void setShipDate(Date shipDate);

        /**
         * Getter for <b>partNum</b>
         * 
         * @return
         *      <b>partNum</<b>
         */
        @JsProperty
        public final native String getPartNum();

        /**
         * Setter for <b>partNum</b>
         * 
         * @param partNum
         *      <b>partNum</<b> to set.
         */
        @JsProperty
        public final native void setPartNum(String partNum);

    }

}
