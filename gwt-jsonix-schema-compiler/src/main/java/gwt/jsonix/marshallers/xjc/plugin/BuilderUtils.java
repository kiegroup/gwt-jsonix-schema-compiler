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
import java.io.IOException;
import java.text.MessageFormat;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.FileCodeWriter;
import com.sun.codemodel.writer.FilterCodeWriter;
import com.sun.tools.xjc.model.Model;
import org.hisrc.jsonix.settings.LogLevelSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to provide common methods
 */
public class BuilderUtils {

    public static final String CALLBACKS = "callbacks";
    public static final String MAIN_JS = "MainJs";
    public static final String MARSHALL_CALLBACK = "MarshallCallback";
    public static final String UNMARSHALL_CALLBACK = "UnmarshallCallback";

    /**
     * Returns a <code>CodeWriter</code> whose target directory will be  Model.options.targetDir
     * @param model
     * @param settings
     * @return
     * @throws Exception
     */
    public static CodeWriter createCodeWriter(Model model, GWTSettings settings) throws Exception {
        try {
            File targetDir = settings.getJsinteropDirectory() != null ? settings.getJsinteropDirectory() : model.options.targetDir;
            return new FileCodeWriter(targetDir, model.options.readOnly, model.options.encoding);
        } catch (IOException e) {
            throw new Exception("Failed to FileCodeWriter", e);
        }
    }

    /**
     * Actually write the generated classes, using a {@link FilterCodeWriter} instantiated from the given <code>CodeWriter</code>
     * @param jCodeModel
     * @param baseCodeWriter
     * @throws Exception
     */
    public static void writeJSInteropCode(JCodeModel jCodeModel, CodeWriter baseCodeWriter) throws Exception {
        log(LogLevelSetting.DEBUG, MessageFormat.format("Writing JSInterop with [{0}].", baseCodeWriter.toString()), null);
        try {
            final CodeWriter codeWriter = new FilterCodeWriter(baseCodeWriter);
            jCodeModel.build(codeWriter);
        } catch (IOException e) {
            throw new Exception("Unable to write files: "
                                        + e.getMessage(), e);
        }
    }

    /**
     * Helper <b>log</b>
     * @param level
     * @param message
     * @param e provide it to log <b>throwable</b>; could be <code>null</code>
     */
    public static void log(LogLevelSetting level, String message, Throwable e) {
        switch (level) {
            case TRACE:
                if (e!= null) {
                    getLog().trace(message, e);
                } else {
                    getLog().trace(message);
                }
                break;
            case INFO:
                if (e!= null) {
                    getLog().info(message, e);
                } else {
                    getLog().info(message);
                }
                break;
            case WARN:
                if (e!= null) {
                    getLog().warn(message, e);
                } else {
                    getLog().warn(message);
                }
                break;
            case DEBUG:
                if (e!= null) {
                    getLog().debug(message, e);
                } else {
                    getLog().debug(message);
                }
                break;
            case ERROR:
                if (e!= null) {
                    getLog().error(message, e);
                } else {
                    getLog().error(message);
                }
                break;
        }
    }

    /**
     * @return <code>Logger</code>
     */
    private static Logger getLog() {
        return LoggerFactory.getLogger(BuilderUtils.class.getName());
    }

}
