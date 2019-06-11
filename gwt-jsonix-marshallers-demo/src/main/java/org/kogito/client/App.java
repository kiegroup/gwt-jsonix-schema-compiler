package org.kogito.client;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import org.kogito.gwt.jsonix.wrapper.Item;
import org.kogito.gwt.jsonix.wrapper.Items;
import org.kogito.gwt.jsonix.wrapper.MainJs;
import org.kogito.gwt.jsonix.wrapper.PO;
import org.kogito.gwt.jsonix.wrapper.USAddress;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class App implements EntryPoint {

    /**
     * The message displayed to the user when the server cannot be reached or
     * returns an error.
     */
    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        final Button changeValueButton = new Button("CHANGE VALUE");
        final Button unmarshallButton = new Button("UNMARSHALL");
        RootPanel.get("changeValueButtonContainer").add(changeValueButton);
        RootPanel.get("unmarshallButtonContainer").add(unmarshallButton);
        MainJs.setValues();
        changeValueButton.addClickHandler(event -> changeValue());
        unmarshallButton.addClickHandler(event -> unmarshalURL());
    }

    private void changeValue() {
        MainJs.setValue("shipTo.name", "SUPERPUPPA");
    }

    private void unmarshalURL() {
        MainJs.unmarshalURL(poObject -> {
            GWT.log("unmarshalURL poObject.toString() " + poObject.toString());
            GWT.log("unmarshalURL poObject.getClass() " + poObject.getClass());
            GWT.log("unmarshalURL poObject.getName() " + poObject.getName());
            PO po = poObject.getValue();
            GWT.log("unmarshalURL po.toString() " + po.toString());
            GWT.log("unmarshalURL po.getClass() " + po.getClass());
            GWT.log("unmarshalURL po.getComment() " + po.getComment());
            final USAddress billTo = po.getBillTo();
            GWT.log("unmarshalURL billTo.getCity() " + billTo.getCity());
            GWT.log("unmarshalURL billTo.getCountry() " + billTo.getCountry());
            GWT.log("unmarshalURL billTo.getName() " + billTo.getName());
            GWT.log("unmarshalURL billTo.getPiripicchio() " + billTo.getPiripicchio());
            GWT.log("unmarshalURL billTo.getState() " + billTo.getState());
            final USAddress shipTo = po.getShipTo();
            GWT.log("unmarshalURL shipTo.getCity() " + shipTo.getCity());
            GWT.log("unmarshalURL shipTo.getCountry() " + shipTo.getCountry());
            GWT.log("unmarshalURL shipTo.getName() " + shipTo.getName());
            GWT.log("unmarshalURL shipTo.getPiripicchio() " + shipTo.getPiripicchio());
            GWT.log("unmarshalURL shipTo.getState() " + shipTo.getState());
            final Items items = po.getItems();
            final Item[] item = items.getItem();
            for (int i = 0; i < item.length; i ++) {
                Item itm = item[i];
                GWT.log("unmarshalURL itm.getComment() " + itm.getComment());
                GWT.log("unmarshalURL itm.getPartNum() " + itm.getPartNum());
                GWT.log("unmarshalURL itm.getProductName() " + itm.getProductName());
                GWT.log("unmarshalURL itm.getQuantity() " + itm.getQuantity());
            }
        });

    }

}