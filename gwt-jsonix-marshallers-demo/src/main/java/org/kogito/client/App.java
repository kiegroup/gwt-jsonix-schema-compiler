package org.kogito.client;

import com.google.gwt.core.client.EntryPoint;
import org.kogito.client.leaflet.MainJs;

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

    private final static double[] positions = {51.505, -0.09};

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        MainJs.setView(positions, 13);
    }
}