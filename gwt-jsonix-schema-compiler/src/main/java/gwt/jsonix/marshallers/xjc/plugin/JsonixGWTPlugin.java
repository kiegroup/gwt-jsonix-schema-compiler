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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Outline;
import gwt.jsonix.marshallers.xjc.plugin.builders.CallbacksBuilder;
import gwt.jsonix.marshallers.xjc.plugin.builders.ContainerObjectBuilder;
import gwt.jsonix.marshallers.xjc.plugin.builders.JSINameBuilder;
import gwt.jsonix.marshallers.xjc.plugin.builders.JsUtilsBuilder;
import gwt.jsonix.marshallers.xjc.plugin.builders.MainJsBuilder;
import gwt.jsonix.marshallers.xjc.plugin.builders.ModelBuilder;
import org.hisrc.jsonix.args4j.PartialCmdLineParser;
import org.hisrc.jsonix.configuration.JsonSchemaConfiguration;
import org.hisrc.jsonix.configuration.MappingConfiguration;
import org.hisrc.jsonix.configuration.ModulesConfiguration;
import org.hisrc.jsonix.configuration.ModulesConfigurationUnmarshaller;
import org.hisrc.jsonix.configuration.OutputConfiguration;
import org.hisrc.jsonix.context.DefaultJsonixContext;
import org.hisrc.jsonix.settings.LogLevelSetting;
import org.hisrc.jsonix.xjc.plugin.JsonixPlugin;
import org.kohsuke.args4j.CmdLineException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import static gwt.jsonix.marshallers.xjc.plugin.builders.BuilderUtils.createCodeWriter;
import static gwt.jsonix.marshallers.xjc.plugin.builders.BuilderUtils.log;
import static gwt.jsonix.marshallers.xjc.plugin.builders.BuilderUtils.writeJSInteropCode;

/**
 * Wrapper class of the original <code>JsonixPlugin</code> that also generates <b>JSInterop</b> code
 */
public class JsonixGWTPlugin extends JsonixPlugin {

    public static final String OPTION_NAME = "Xgwtjsonix";

    private GWTSettings settings = new GWTSettings();

    @Override
    public GWTSettings getSettings() {
        return settings;
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
            throws BadCommandLineException {

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
        log(LogLevelSetting.DEBUG, "run");
        try {
            Model model = outline.getModel();
            JCodeModel jCodeModel = new JCodeModel();
            final CodeWriter codeWriter = createCodeWriter(model, getSettings());
            final JDefinedClass jsiNameClass = JSINameBuilder.generateJSINameClass(jCodeModel, settings.getJsMainPackage());
            final JDefinedClass jsUtilsClass = JsUtilsBuilder.generateJsUtilsClass(jCodeModel, settings.getJsMainPackage());
            Map<String, String> packageModuleMap = getPackageModuleMap(model);
            Map<String, JClass> definedClassesMap = new HashMap<>();
            ModelBuilder.generateJSInteropModels(definedClassesMap, model, jCodeModel, packageModuleMap, jsUtilsClass, jsiNameClass);
            Map<String, Map<String, JClass>> topLevelElementsMap = getTopLevelElementsMap(packageModuleMap.keySet(), definedClassesMap, model.getAllElements());
            final List<JDefinedClass> containersClasses = ContainerObjectBuilder.generateJSInteropContainerObjects(packageModuleMap, topLevelElementsMap, jCodeModel);
            final Map<String, Map<String, JDefinedClass>> callbacksMap = CallbacksBuilder.generateJSInteropCallbacks(containersClasses, jCodeModel);
            MainJsBuilder.generateJSInteropMainJs(callbacksMap, containersClasses, jCodeModel);
            writeJSInteropCode(jCodeModel, codeWriter);
        } catch (Exception e) {
            log(LogLevelSetting.ERROR, e.getMessage(), e);
            throw new SAXException(e);
        }
        return true;
    }

    @Override
    public void postProcessModel(Model model, ErrorHandler errorHandler) {
        //
    }

    protected Map<String, Map<String, JClass>> getTopLevelElementsMap(Set<String> packageNames, final Map<String, JClass> definedClassesMap, Iterable<? extends CElementInfo> allElements) {
        log(LogLevelSetting.DEBUG, "getTopLevelElementsMap");
        Spliterator<? extends CElementInfo> spliterator = allElements.spliterator();
        final List<? extends CElementInfo> allElementsList = StreamSupport.stream(spliterator, false).collect(Collectors.toList());
        Map<String, Map<String, JClass>> toReturn = new HashMap<>();
        for (String packageName : packageNames) {
            Map<String, JClass> toPut = allElementsList.stream()
                    .filter(cElementInfo -> Objects.equals(packageName, cElementInfo._package().name()))
                    .filter(cElementInfo -> definedClassesMap.containsKey(cElementInfo.getContentType().getType().fullName()))
                    .collect(Collectors.toMap(cElementInfo -> cElementInfo.getElementName().getLocalPart(),
                                              cElementInfo -> definedClassesMap.get(cElementInfo.getContentType().getType().fullName())));
            toReturn.put(packageName, toPut);
        }
        return toReturn;
    }

    protected Map<String, String> getPackageModuleMap(Model model) {
        log(LogLevelSetting.DEBUG, "getPackageModuleMap");
        GWTSettings gwtSettings = getSettings();
        final DefaultJsonixContext context = new DefaultJsonixContext();
        final OutputConfiguration defaultOutputConfiguration = new OutputConfiguration(
                gwtSettings.getDefaultNaming().getName(),
                OutputConfiguration.STANDARD_FILE_NAME_PATTERN);
        final JsonSchemaConfiguration defaultJsonSchemaConfiguration = gwtSettings
                .isGenerateJsonSchema() ? new JsonSchemaConfiguration(
                JsonSchemaConfiguration.STANDARD_FILE_NAME_PATTERN) : null;
        final ModulesConfigurationUnmarshaller customizationHandler = new ModulesConfigurationUnmarshaller(context);
        ModulesConfiguration modulesConfiguration = customizationHandler.unmarshal(model, defaultOutputConfiguration, defaultJsonSchemaConfiguration);
        return modulesConfiguration.getMappingConfigurations().stream().collect(Collectors.toMap(MappingConfiguration::getPackage, MappingConfiguration::getName));
    }
}
