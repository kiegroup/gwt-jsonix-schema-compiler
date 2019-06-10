package org.kogito.client.leaflet;

import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = "L")
public class Map {

    public native L setView(double[] center, int zoom);
}