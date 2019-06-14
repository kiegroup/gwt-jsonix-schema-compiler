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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CNonElement;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Outline;
import org.hisrc.jsonix.args4j.PartialCmdLineParser;
import org.hisrc.jsonix.configuration.JsonSchemaConfiguration;
import org.hisrc.jsonix.configuration.MappingConfiguration;
import org.hisrc.jsonix.configuration.ModulesConfiguration;
import org.hisrc.jsonix.configuration.ModulesConfigurationUnmarshaller;
import org.hisrc.jsonix.configuration.OutputConfiguration;
import org.hisrc.jsonix.context.DefaultJsonixContext;
import org.hisrc.jsonix.xjc.plugin.JsonixPlugin;
import org.kohsuke.args4j.CmdLineException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import static gwt.jsonix.marshallers.xjc.plugin.BuilderUtils.createCodeWriter;
import static gwt.jsonix.marshallers.xjc.plugin.BuilderUtils.writeJSInteropCode;

/**
 * Wrapper class of the original <code>JsonixPlugin</code> that also generates <b>JSInterop</b> code
 */
public class JsonixGWTPlugin extends JsonixPlugin {

    public static final String OPTION_NAME = "Xgwtjsonix";
    public static final String OPTION = "-" + OPTION_NAME;

    private GWTSettings settings = new GWTSettings();

    @Override
    public GWTSettings getSettings() {
        return settings;
    }

    public void setSettings(GWTSettings settings) {
        this.settings = settings;
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    @Override
    public String getUsage() {
        return "  -Xgwtjsonix :  Generates Jsonix mappings and JSInterop code.\n"
                + "                    See (to_be_defined)";
    }

    @Override
    public int parseArgument(Options opt, String[] args, int i)
            throws BadCommandLineException, IOException {

        final PartialCmdLineParser cmdLineParser = new PartialCmdLineParser(
                getSettings());
        try {
            return cmdLineParser.parseArgument(args, i);
        } catch (CmdLineException clex) {
            throw new BadCommandLineException("Error parsing arguments.", clex);
        }
    }

    @Override
    public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) throws SAXException {
        super.run(outline, opt, errorHandler);
        log(Level.FINE, "run", null);
        try {
            Model model = outline.getModel();
            JCodeModel jCodeModel = new JCodeModel();
            final CodeWriter codeWriter = createCodeWriter(model, getSettings());
            Map<String, String> packageModuleMap = getPackageModuleMap(model);
            Map<String, JDefinedClass> definedClassesMap = new HashMap<>();
            ModelBuilder.generateJSInteropModels(definedClassesMap, model, jCodeModel);
            List<JDefinedClass> mainObjectsList = getMainObjectsList(definedClassesMap, model.getAllElements());
            final List<JDefinedClass> containersClasses = ContainerObjectBuilder.generateJSInteropContainerObjects(packageModuleMap, mainObjectsList, jCodeModel);
            final Map<String, Map<String, JDefinedClass>> callbacksMap = CallbacksBuilder.generateJSInteropCallbacks(containersClasses, jCodeModel);
            MainJsBuilder.generateJSInteropMainJs(callbacksMap, containersClasses, jCodeModel);
            writeJSInteropCode(jCodeModel, codeWriter);
        } catch (Exception e) {
            log(Level.SEVERE, e.getMessage(), e);
            throw new SAXException(e);
        }
        return true;
    }

    @Override
    public void postProcessModel(Model model, ErrorHandler errorHandler) {
        //
    }

    protected List<JDefinedClass> getMainObjectsList(final Map<String, JDefinedClass> definedClassesMap, Iterable<? extends CElementInfo> allElements) {
        log(Level.FINE, "getMainObjectsList", null);
        Spliterator<? extends CElementInfo> spliterator = allElements.spliterator();
        return StreamSupport.stream(spliterator, false)
                .map(CElementInfo::getContentType)
                .map(CNonElement::getType)
                .filter(type -> definedClassesMap.containsKey(type.fullName()))
                .map(type -> definedClassesMap.get(type.fullName()))
                .collect(Collectors.toList());
    }

    protected Map<String, String> getPackageModuleMap(Model model) {
        log(Level.FINE, "getPackageModuleMap", null);
        GWTSettings settings = getSettings();
        final DefaultJsonixContext context = new DefaultJsonixContext();
        final OutputConfiguration defaultOutputConfiguration = new OutputConfiguration(
                settings.getDefaultNaming().getName(),
                OutputConfiguration.STANDARD_FILE_NAME_PATTERN);
        final JsonSchemaConfiguration defaultJsonSchemaConfiguration = settings
                .isGenerateJsonSchema() ? new JsonSchemaConfiguration(
                JsonSchemaConfiguration.STANDARD_FILE_NAME_PATTERN) : null;
        final ModulesConfigurationUnmarshaller customizationHandler = new ModulesConfigurationUnmarshaller(context);
        ModulesConfiguration modulesConfiguration = customizationHandler.unmarshal(model, defaultOutputConfiguration, defaultJsonSchemaConfiguration);
        return modulesConfiguration.getMappingConfigurations().stream().collect(Collectors.toMap(MappingConfiguration::getPackage, MappingConfiguration::getName));
    }

    protected void log(Level level, String message, Exception e) {
        if (e != null) {
            getLog().log(level, message, e);
        } else {
            getLog().log(level, message);
        }
    }

    protected Logger getLog() {
        return Logger.getLogger(JsonixGWTPlugin.class.getName());
    }
}
