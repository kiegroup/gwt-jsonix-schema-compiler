MainJs = {
    setValues: function () {
        console.log("outer setValues");
        require(["main"], function (main) {
            console.log("inner setValues " + main);
            main.setValues();
        });
    },

    setValue: function (id, value) {
        console.log("outer setValue " + id + " " + value);
        require(["main"], function (main) {
            console.log("inner setValue " +  main + " " + id + " " + value);
            main.setValue(id, value);
        });
    },

    unmarshalURL: function (callback) {
        console.log("outer unmarshalURL");
        require(["main"], function (main) {
            console.log("inner unmarshalURL");
            return main.unmarshalURL(callback);
        });
    }
}
