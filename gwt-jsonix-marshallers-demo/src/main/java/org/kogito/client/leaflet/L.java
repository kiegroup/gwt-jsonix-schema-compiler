package org.kogito.client.leaflet;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class L {

    public static native Map map(String id);
}