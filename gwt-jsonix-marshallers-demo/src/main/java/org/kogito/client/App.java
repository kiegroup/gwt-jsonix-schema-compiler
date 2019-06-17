package org.kogito.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import org.kogito.gwt.jsonix.marshaller.js.MainJs;
import org.kogito.gwt.jsonix.marshaller.js.callbacks.SCESIMMarshallCallback;
import org.kogito.gwt.jsonix.marshaller.js.callbacks.SCESIMUnmarshallCallback;
import org.kogito.gwt.jsonix.marshaller.js.model.JSIScenarioSimulationModelType;
import org.kogito.gwt.jsonix.marshaller.js.model.SCESIM;

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
    private static final String poXml =
            "<ScenarioSimulationModel version=\"1.4\">\n" +
                    "  <simulation>\n" +
                    "    <simulationDescriptor>\n" +
                    "      <factMappings>\n" +
                    "        <FactMapping>\n" +
                    "          <expressionElements>\n" +
                    "            <ExpressionElement>\n" +
                    "              <step>#</step>\n" +
                    "            </ExpressionElement>\n" +
                    "          </expressionElements>\n" +
                    "          <expressionIdentifier>\n" +
                    "            <name>Index</name>\n" +
                    "            <type>OTHER</type>\n" +
                    "          </expressionIdentifier>\n" +
                    "          <factIdentifier>\n" +
                    "            <name>#</name>\n" +
                    "            <className>java.lang.Integer</className>\n" +
                    "          </factIdentifier>\n" +
                    "          <className>java.lang.Integer</className>\n" +
                    "          <factAlias>#</factAlias>\n" +
                    "        </FactMapping>\n" +
                    "        <FactMapping>\n" +
                    "          <expressionElements>\n" +
                    "            <ExpressionElement>\n" +
                    "              <step>Scenario description</step>\n" +
                    "            </ExpressionElement>\n" +
                    "          </expressionElements>\n" +
                    "          <expressionIdentifier>\n" +
                    "            <name>Description</name>\n" +
                    "            <type>OTHER</type>\n" +
                    "          </expressionIdentifier>\n" +
                    "          <factIdentifier>\n" +
                    "            <name>Scenario description</name>\n" +
                    "            <className>java.lang.String</className>\n" +
                    "          </factIdentifier>\n" +
                    "          <className>java.lang.String</className>\n" +
                    "          <factAlias>Scenario description</factAlias>\n" +
                    "        </FactMapping>\n" +
                    "        <FactMapping>\n" +
                    "          <expressionElements>\n" +
                    "            <ExpressionElement>\n" +
                    "              <step>1545231213318</step>\n" +
                    "            </ExpressionElement>\n" +
                    "          </expressionElements>\n" +
                    "          <expressionIdentifier>\n" +
                    "            <name>1545231213318</name>\n" +
                    "            <type>GIVEN</type>\n" +
                    "          </expressionIdentifier>\n" +
                    "          <factIdentifier>\n" +
                    "            <name>1545231213318</name>\n" +
                    "            <className>java.lang.Boolean</className>\n" +
                    "          </factIdentifier>\n" +
                    "          <className>java.lang.Boolean</className>\n" +
                    "          <factAlias>Boolean</factAlias>\n" +
                    "          <expressionAlias>value</expressionAlias>\n" +
                    "        </FactMapping>\n" +
                    "        <FactMapping>\n" +
                    "          <expressionElements>\n" +
                    "            <ExpressionElement>\n" +
                    "              <step>1545231216876</step>\n" +
                    "            </ExpressionElement>\n" +
                    "          </expressionElements>\n" +
                    "          <expressionIdentifier>\n" +
                    "            <name>1545231216876</name>\n" +
                    "            <type>EXPECT</type>\n" +
                    "          </expressionIdentifier>\n" +
                    "          <factIdentifier>\n" +
                    "            <name>1545231216876</name>\n" +
                    "            <className>java.lang.Integer</className>\n" +
                    "          </factIdentifier>\n" +
                    "          <className>java.lang.Integer</className>\n" +
                    "          <factAlias>Integer</factAlias>\n" +
                    "          <expressionAlias>value</expressionAlias>\n" +
                    "        </FactMapping>\n" +
                    "      </factMappings>\n" +
                    "      <dmoSession>default</dmoSession>\n" +
                    "      <type>RULE</type>\n" +
                    "      <fileName></fileName>\n" +
                    "      <kieSession>default</kieSession>\n" +
                    "      <kieBase>default</kieBase>\n" +
                    "      <ruleFlowGroup>default</ruleFlowGroup>\n" +
                    "    </simulationDescriptor>\n" +
                    "    <scenarios>\n" +
                    "      <Scenario>\n" +
                    "        <factMappingValues>\n" +
                    "          <FactMappingValue>\n" +
                    "            <factIdentifier reference=\"../../../../../simulationDescriptor/factMappings/FactMapping[2]/factIdentifier\"/>\n" +
                    "            <expressionIdentifier reference=\"../../../../../simulationDescriptor/factMappings/FactMapping[2]/expressionIdentifier\"/>\n" +
                    "          </FactMappingValue>\n" +
                    "          <FactMappingValue>\n" +
                    "            <factIdentifier reference=\"../../../../../simulationDescriptor/factMappings/FactMapping/factIdentifier\"/>\n" +
                    "            <expressionIdentifier reference=\"../../../../../simulationDescriptor/factMappings/FactMapping/expressionIdentifier\"/>\n" +
                    "            <rawValue class=\"string\">1</rawValue>\n" +
                    "          </FactMappingValue>\n" +
                    "          <FactMappingValue>\n" +
                    "            <factIdentifier reference=\"../../../../../simulationDescriptor/factMappings/FactMapping[3]/factIdentifier\"/>\n" +
                    "            <expressionIdentifier reference=\"../../../../../simulationDescriptor/factMappings/FactMapping[3]/expressionIdentifier\"/>\n" +
                    "            <rawValue class=\"string\">true</rawValue>\n" +
                    "          </FactMappingValue>\n" +
                    "          <FactMappingValue>\n" +
                    "            <factIdentifier reference=\"../../../../../simulationDescriptor/factMappings/FactMapping[4]/factIdentifier\"/>\n" +
                    "            <expressionIdentifier reference=\"../../../../../simulationDescriptor/factMappings/FactMapping[4]/expressionIdentifier\"/>\n" +
                    "            <rawValue class=\"string\">1</rawValue>\n" +
                    "          </FactMappingValue>\n" +
                    "        </factMappingValues>\n" +
                    "        <simulationDescriptor reference=\"../../../simulationDescriptor\"/>\n" +
                    "      </Scenario>\n" +
                    "    </scenarios>\n" +
                    "  </simulation>\n" +
                    "  <imports>\n" +
                    "    <imports>\n" +
                    "      <Import>\n" +
                    "        <type>java.lang.Integer</type>\n" +
                    "      </Import>\n" +
                    "      <Import>\n" +
                    "        <type>java.lang.String</type>\n" +
                    "      </Import>\n" +
                    "      <Import>\n" +
                    "        <type>java.lang.Boolean</type>\n" +
                    "      </Import>\n" +
                    "    </imports>\n" +
                    "  </imports>\n" +
                    "</ScenarioSimulationModel>";
    private SCESIM SCESIM_OBJECT = null;

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
        SCESIMUnmarshallCallback callback = scesimObject -> {
            SCESIM_OBJECT = scesimObject;
            final JSIScenarioSimulationModelType scenarioSimulationModelType = scesimObject.getValue();
            GWT.log("unmarshall scenarioSimulationModelType.toString() " + scenarioSimulationModelType.toString());
            GWT.log("unmarshall scenarioSimulationModelType.getClass() " + scenarioSimulationModelType.getClass());
            GWT.log("unmarshall scenarioSimulationModelType.getVersion() " + scenarioSimulationModelType.getVersion());
//            final JSIUSAddress shipTo = scenarioSimulationModelType.getShipTo();
//            GWT.log("unmarshall shipTo.getName() " + shipTo.getName());
//            RootPanel.get("shipTo.name").getElement().setInnerText(shipTo.getName());
//            GWT.log("unmarshall shipTo.getSurname() " + shipTo.getSurname());
//            RootPanel.get("shipTo.surname").getElement().setInnerText(shipTo.getSurname());
//            GWT.log("unmarshall shipTo.getCity() " + shipTo.getCity());
//            RootPanel.get("shipTo.city").getElement().setInnerText(shipTo.getCity());
//            GWT.log("unmarshall shipTo.getState() " + shipTo.getState());
//            RootPanel.get("shipTo.state").getElement().setInnerText(shipTo.getState());
//            GWT.log("unmarshall shipTo.getCountry() " + shipTo.getCountry());
//            RootPanel.get("shipTo.country").getElement().setInnerText(shipTo.getCountry());
//
//            final JSIUSAddress billTo = scenarioSimulationModelType.getBillTo();
//            GWT.log("unmarshall billTo.getName() " + billTo.getName());
//            RootPanel.get("billTo.name").getElement().setInnerText(billTo.getName());
//            GWT.log("unmarshall billTo.getSurname() " + billTo.getSurname());
//            RootPanel.get("billTo.surname").getElement().setInnerText(billTo.getSurname());
//            GWT.log("unmarshall billTo.getCity() " + billTo.getCity());
//            RootPanel.get("billTo.city").getElement().setInnerText(billTo.getCity());
//            GWT.log("unmarshall billTo.getState() " + billTo.getState());
//            RootPanel.get("billTo.state").getElement().setInnerText(billTo.getState());
//            GWT.log("unmarshall billTo.getCountry() " + billTo.getCountry());
//            RootPanel.get("billTo.country").getElement().setInnerText(billTo.getCountry());
//            final JSIItems items = scenarioSimulationModelType.getItems();
//            final JSIItems.Item[] item = items.getItem();
//            BigDecimal totalPrice = new BigDecimal(0);
//            for (int i = 0; i < item.length; i ++) {
//                JSIItems.Item itm = item[i];
//                GWT.log("unmarshall itm.getComment() " + itm.getComment());
//                GWT.log("unmarshall itm.getPartNum() " + itm.getPartNum());
//                GWT.log("unmarshall itm.getProductName() " + itm.getProductName());
//                GWT.log("unmarshall itm.getQuantity() " + itm.getQuantity());
//                GWT.log("unmarshall itm.getUSPrice() " + itm.getUSPrice());
//                totalPrice = totalPrice.add(itm.getUSPrice());
//            }
//            RootPanel.get("items.quantity").getElement().setInnerText(String.valueOf(item.length));
//            RootPanel.get("items.usPrice").getElement().setInnerText(String.valueOf(totalPrice));
        };
        MainJs.unmarshall(poXml, callback);
    }

    private void marshall() {
        SCESIMMarshallCallback callback = xmlString -> {
            GWT.log("marshall xmlString " + xmlString);
            SCESIM_OBJECT = null;
        };
        MainJs.marshall(SCESIM_OBJECT, callback);
    }
}