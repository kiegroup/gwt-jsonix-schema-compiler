package org.kogito.client;

import java.math.BigDecimal;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import org.kogito.gwt.jsonix.marshaller.MainJs;
import org.kogito.gwt.jsonix.marshaller.callbacks.POMarshallCallback;
import org.kogito.gwt.jsonix.marshaller.callbacks.POUnmarshallCallback;
import org.kogito.gwt.jsonix.marshaller.po.JSIItems;
import org.kogito.gwt.jsonix.marshaller.po.JSIPurchaseOrderType;
import org.kogito.gwt.jsonix.marshaller.po.JSIUSAddress;
import org.kogito.gwt.jsonix.marshaller.po.PO;


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

    private PO PO_OBJECT = null;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        final Button unmarshallButton = new Button("UNMARSHALL");
        final Button marshallButton = new Button("MARSHALL");
        RootPanel.get("unmarshallButtonContainer").add(unmarshallButton);
        RootPanel.get("marshallButtonContainer").add(marshallButton);
        marshallButton.setEnabled(false);
        unmarshallButton.addClickHandler(event -> {
            unmarshall();
            marshallButton.setEnabled(true);
            unmarshallButton.setEnabled(false);
        });
        marshallButton.addClickHandler(event -> {
            marshall();
            marshallButton.setEnabled(false);
            unmarshallButton.setEnabled(true);
        });

    }

    private void unmarshall() {
        POUnmarshallCallback callback = pObject -> {
            PO_OBJECT = pObject;
            final JSIPurchaseOrderType po = pObject.getValue();
            GWT.log("unmarshall po.toString() " + po.toString());
            GWT.log("unmarshall po.getClass() " + po.getClass());
            GWT.log("unmarshall po.getComment() " + po.getComment());
            final JSIUSAddress shipTo = po.getShipTo();
            GWT.log("unmarshall shipTo.getName() " + shipTo.getName());
            RootPanel.get("shipTo.name").getElement().setInnerText(shipTo.getName());
            GWT.log("unmarshall shipTo.getSurname() " + shipTo.getSurname());
            RootPanel.get("shipTo.surname").getElement().setInnerText(shipTo.getSurname());
            GWT.log("unmarshall shipTo.getCity() " + shipTo.getCity());
            RootPanel.get("shipTo.city").getElement().setInnerText(shipTo.getCity());
            GWT.log("unmarshall shipTo.getState() " + shipTo.getState());
            RootPanel.get("shipTo.state").getElement().setInnerText(shipTo.getState());
            GWT.log("unmarshall shipTo.getCountry() " + shipTo.getCountry());
            RootPanel.get("shipTo.country").getElement().setInnerText(shipTo.getCountry());

            final JSIUSAddress billTo = po.getBillTo();
            GWT.log("unmarshall billTo.getName() " + billTo.getName());
            RootPanel.get("billTo.name").getElement().setInnerText(billTo.getName());
            GWT.log("unmarshall billTo.getSurname() " + billTo.getSurname());
            RootPanel.get("billTo.surname").getElement().setInnerText(billTo.getSurname());
            GWT.log("unmarshall billTo.getCity() " + billTo.getCity());
            RootPanel.get("billTo.city").getElement().setInnerText(billTo.getCity());
            GWT.log("unmarshall billTo.getState() " + billTo.getState());
            RootPanel.get("billTo.state").getElement().setInnerText(billTo.getState());
            GWT.log("unmarshall billTo.getCountry() " + billTo.getCountry());
            RootPanel.get("billTo.country").getElement().setInnerText(billTo.getCountry());
            final JSIItems items = po.getItems();
            final JSIItems.Item[] item = items.getItem();
            BigDecimal totalPrice = new BigDecimal(0);
            for (int i = 0; i < item.length; i ++) {
                JSIItems.Item itm = item[i];
                GWT.log("unmarshall itm.getComment() " + itm.getComment());
                GWT.log("unmarshall itm.getPartNum() " + itm.getPartNum());
                GWT.log("unmarshall itm.getProductName() " + itm.getProductName());
                GWT.log("unmarshall itm.getQuantity() " + itm.getQuantity());
                GWT.log("unmarshall itm.getUSPrice() " + itm.getUSPrice());
                totalPrice = totalPrice.add(itm.getUSPrice());
            }
            RootPanel.get("items.quantity").getElement().setInnerText(String.valueOf(item.length));
            RootPanel.get("items.usPrice").getElement().setInnerText(String.valueOf(totalPrice));
        };
        MainJs.unmarshall(poXml, callback);
    }

    private void marshall() {
        POMarshallCallback callback = xmlString -> {
            GWT.log("marshall xmlString " + xmlString);
            PO_OBJECT = null;
        };
        MainJs.marshall(PO_OBJECT, callback);
    }

    private static final String poXml = "" +
            "<purchaseOrder orderDate=\"1999-10-20\">\n" +
            "  <shipTo country=\"US\">\n" +
            "    <name>Alice Smith</name>\n" +
            "    <piripicchio>123 Maple Street</piripicchio>\n" +
            "    <city>Mill Valley</city>\n" +
            "    <state>CA</state>\n" +
            "    <zip>90952</zip>\n" +
            "  </shipTo>\n" +
            "  <billTo country=\"US\">\n" +
            "    <name>Robert Smith</name>\n" +
            "    <piripicchio>8 Oak Avenue</piripicchio>\n" +
            "    <city>Old Town</city>\n" +
            "    <state>PA</state>\n" +
            "    <zip>95819</zip>\n" +
            "  </billTo>\n" +
            "  <comment>Hurry, my lawn is going wild!</comment>\n" +
            "  <items>\n" +
            "    <item partNum=\"872-AA\">\n" +
            "      <productName>Lawnmower</productName>\n" +
            "      <quantity>1</quantity>\n" +
            "      <USPrice>148.95</USPrice>\n" +
            "      <comment>Confirm this is electric</comment>\n" +
            "    </item>\n" +
            "    <item partNum=\"926-AA\">\n" +
            "      <productName>Baby Monitor</productName>\n" +
            "      <quantity>1</quantity>\n" +
            "      <USPrice>39.98</USPrice>\n" +
            "      <shipDate>1999-05-21</shipDate>\n" +
            "    </item>\n" +
            "  </items>\n" +
            "</purchaseOrder>";

}