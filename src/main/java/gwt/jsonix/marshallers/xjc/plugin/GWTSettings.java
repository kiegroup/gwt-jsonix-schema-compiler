/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gwt.jsonix.marshallers.xjc.plugin;

import java.io.File;

import org.hisrc.jsonix.settings.Settings;
import org.kohsuke.args4j.Option;

public class GWTSettings extends Settings {

    private File jsinteropDirectory;

    private String jsMainPackage = "";

    public File getJsinteropDirectory() {
        return jsinteropDirectory;
    }

    public String getJsMainPackage() {
        return jsMainPackage;
    }

    @Option(name = "-jsid", aliases = {"-XjsinteropDirectory"})
    public void setJsinteropDirectory(File jsinteropDirectory) {
        this.jsinteropDirectory = jsinteropDirectory;
    }

    @Option(name = "-jsmpkg", aliases = {"-XjsMainPackage"})
    public void setJsMainPackage(String jsMainPackage) {
        this.jsMainPackage = jsMainPackage;
    }
}
