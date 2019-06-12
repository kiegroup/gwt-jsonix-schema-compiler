define(["Jsonix-all", "PO"], function (JsonixModule, POModule) {

    var Jsonix = JsonixModule.Jsonix;
    var PO = POModule.PO;

    var setValueLocal = function (id, value) {
        var element = document.getElementById(id);
        element.innerHTML = value;
    }

    return {

        unmarshalURL: function (callback) {
            // // Jsonix usage

            // Create Jsonix context
            var context = new Jsonix.Context([PO]);

            // Create unmarshaller
            var unmarshaller = context.createUnmarshaller();
            unmarshaller.unmarshalURL('po.xml', callback);
        },

        marshalDocument: function (value) {
            // // Jsonix usage

            // Create Jsonix context
            var context = new Jsonix.Context([PO]);

            // Create unmarshaller
            var marshaller = context.createMarshaller();
            return marshaller.marshalDocument(value);
        },

        setValues: function () {

            // // Jsonix usage

            // Create Jsonix context
            var context = new Jsonix.Context([PO]);

            // Create unmarshaller
            var unmarshaller = context.createUnmarshaller();

            // Unmarshal the XML file from URL
            unmarshaller.unmarshalURL('po.xml', function (poElement) {

                // That's it, unmarshalling is complete

                // Now we can worked with the unmarshalled object
                var po = poElement.value;

                // Output shipping and billing address

                setValueLocal('shipTo.name', po.shipTo.name);
                setValueLocal('shipTo.piripicchio', po.shipTo.piripicchio);
                setValueLocal('shipTo.city', po.shipTo.city);
                setValueLocal('shipTo.state', po.shipTo.state);
                setValueLocal('shipTo.country', po.shipTo.country);

                setValueLocal('billTo.name', po.billTo.name);
                setValueLocal('billTo.piripicchio', po.billTo.piripicchio);
                setValueLocal('billTo.city', po.billTo.city);
                setValueLocal('billTo.state', po.billTo.state);
                setValueLocal('billTo.country', po.billTo.country);

                // Calculate and output the total quantity and price
                var quantity = 0;
                var usPrice = 0;

                for (var index = 0; index < po.items.item.length; index++) {
                    var item = po.items.item[index];
                    quantity += item.quantity;
                    usPrice += item.usPrice;
                }

                setValueLocal('items.quantity', quantity.toFixed(0));
                setValueLocal('items.usPrice', usPrice.toFixed(2));

                // Format and output the order date
                setValueLocal('orderDate',
                        // We can use the method of the calendar type here
                        Jsonix.Schema.XSD.Calendar.INSTANCE.printDate(po.orderDate));

            });
        },

        setValue: function (id, value) {
            setValueLocal(id, value);
        }
    };

});
