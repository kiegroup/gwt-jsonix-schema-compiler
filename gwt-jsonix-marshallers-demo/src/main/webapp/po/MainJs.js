define('mainLocal', ["Jsonix-all", "PO"], function (JsonixModule, POModule) {

    var Jsonix = JsonixModule.Jsonix;
    var PO = POModule.PO;
    return {
        unmarshallLocal: function (text, callback) {
            console.log("unmarshallLocal");
            // Create Jsonix context
            var context = new Jsonix.Context([PO]);

            // Create unmarshaller
            var unmarshaller = context.createUnmarshaller();
            var toReturn = unmarshaller.unmarshalString(text);
            callback(toReturn);
        },

        marshallLocal: function (value, callback) {
            console.log("marshallLocal");
            // Create Jsonix context
            var context = new Jsonix.Context([PO]);

            // Create unmarshaller
            var marshaller = context.createMarshaller();

            var xmlDocument = marshaller.marshalDocument(value);
            var s = new XMLSerializer();
            var toReturn = s.serializeToString(xmlDocument);
            callback(toReturn);
        }
    }
});

MainJs = {
    unmarshall: function (text, callback) {
        console.log("out unmarshall");
        require(["mainLocal"], function(mainLocal) {
            console.log("inner unmarshall");
            mainLocal.unmarshallLocal(text, callback);
        });
    },

    marshall: function (value, callback) {
        console.log("outer marshall");
        require(["mainLocal"], function(mainLocal) {
            console.log("inner unmarshall");
            mainLocal.marshallLocal(value, callback);
        });
    }
}