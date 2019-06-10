MainJs = {
    setView: function (center, zoom) {
        console.log(center + " puppa " + zoom);
        L.map('map').setView(center, zoom);
    }
}