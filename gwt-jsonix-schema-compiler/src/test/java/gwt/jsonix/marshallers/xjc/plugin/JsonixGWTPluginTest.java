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

import java.util.Map;

import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.Model;
import org.junit.Before;
import org.junit.Test;

import static gwt.jsonix.marshallers.xjc.plugin.TestUtils.getModel;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class JsonixGWTPluginTest {

    private JsonixGWTPlugin jsonixGWTPlugin;

    @Before
    public void setup() {
        jsonixGWTPlugin = new JsonixGWTPlugin();
    }

    @Test
    public void parseArgumentCorrect() {
        String[] args = {"-Xgwtjsonix",
                "-Xgwtjsonix",
                "-Xinheritance",
                "-Xnamespace-prefix",
                "-jsid=target/project",
                "-jsmpkg=org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper"
        };
        try {
            for (int i = 0; i < args.length; i++) {
                jsonixGWTPlugin.parseArgument(new Options(), args, i);
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test(expected = BadCommandLineException.class)
    public void parseArgumentWrong() throws BadCommandLineException {
        String[] args = {"-Xgwtjsonix",
                "-Xgwtjsonix",
                "-Xinheritance",
                "-Xnamespace-prefix",
                "-jsid=target/project",
                "-jsmpkg"
        };
        for (int i = 0; i < args.length; i++) {
            jsonixGWTPlugin.parseArgument(new Options(), args, i);
        }
    }

    @Test
    public void getPackageModuleMap() {
        final Map<String, String> packageModuleMap = jsonixGWTPlugin.getPackageModuleMap(getModel());
        assertNotNull(packageModuleMap);
    }
}