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

package trial.execution.js.model.kie;

import com.google.gwt.core.client.ScriptInjector;
import jsinterop.base.JsPropertyMap;
import trial.execution.js.model.MainJs;
import trial.execution.mapper.AbstractGWTTestCase;
import trial.execution.mapper.JSIName;

@SuppressWarnings("cast")
public abstract class AbstractModelTest extends AbstractGWTTestCase {

    private final static String KIE_JS = "var KIE_Module_Factory = function () {\n" +
            "  var KIE = {\n" +
            "    name: 'KIE',\n" +
            "    defaultElementNamespaceURI: 'http:\\/\\/www.drools.org\\/kie\\/dmn\\/1.2',\n" +
            "    typeInfos: [{\n" +
            "        localName: 'TComponentsWidthsExtension',\n" +
            "        typeName: 'tComponentsWidthsExtension',\n" +
            "        propertyInfos: [{\n" +
            "            name: 'componentWidths',\n" +
            "            minOccurs: 0,\n" +
            "            collection: true,\n" +
            "            elementName: 'ComponentWidths',\n" +
            "            typeInfo: '.TComponentWidths'\n" +
            "          }]\n" +
            "      }, {\n" +
            "        localName: 'TComponentWidths',\n" +
            "        typeName: 'tComponentWidths',\n" +
            "        propertyInfos: [{\n" +
            "            name: 'width',\n" +
            "            minOccurs: 0,\n" +
            "            collection: true,\n" +
            "            typeInfo: 'Float'\n" +
            "          }, {\n" +
            "            name: 'dmnElementRef',\n" +
            "            attributeName: {\n" +
            "              localPart: 'dmnElementRef'\n" +
            "            },\n" +
            "            type: 'attribute'\n" +
            "          }]\n" +
            "      }, {\n" +
            "        localName: 'TAttachment',\n" +
            "        typeName: 'tAttachment',\n" +
            "        propertyInfos: [{\n" +
            "            name: 'value',\n" +
            "            type: 'value'\n" +
            "          }, {\n" +
            "            name: 'url',\n" +
            "            attributeName: {\n" +
            "              localPart: 'url'\n" +
            "            },\n" +
            "            type: 'attribute'\n" +
            "          }, {\n" +
            "            name: 'name',\n" +
            "            attributeName: {\n" +
            "              localPart: 'name'\n" +
            "            },\n" +
            "            type: 'attribute'\n" +
            "          }]\n" +
            "      }],\n" +
            "    elementInfos: [{\n" +
            "        typeInfo: '.TComponentsWidthsExtension',\n" +
            "        elementName: 'ComponentsWidthsExtension'\n" +
            "      }, {\n" +
            "        typeInfo: '.TAttachment',\n" +
            "        elementName: 'attachment'\n" +
            "      }, {\n" +
            "        typeInfo: '.TComponentWidths',\n" +
            "        elementName: 'ComponentWidths'\n" +
            "      }]\n" +
            "  };\n" +
            "  return {\n" +
            "    KIE: KIE\n" +
            "  };\n" +
            "};\n" +
            "if (typeof define === 'function' && define.amd) {\n" +
            "  define([], KIE_Module_Factory);\n" +
            "}\n" +
            "else {\n" +
            "  var KIE_Module = KIE_Module_Factory();\n" +
            "  if (typeof module !== 'undefined' && module.exports) {\n" +
            "    module.exports.KIE = KIE_Module.KIE;\n" +
            "  }\n" +
            "  else {\n" +
            "    var KIE = KIE_Module.KIE;\n" +
            "  }\n" +
            "}";

    private final static String MAIN_JS =  "MainJs = {\n" +
            "    mappings: [KIE],\n" +
            "    initializeJsInteropConstructors: function (constructorsMap) {\n" +
            "        var extraTypes = [{typeName: 'Name', namespace: null}];\n" +
            "        function createFunction(typeName) {\n" +
            "            return new Function('return { \"TYPE_NAME\" : \"' + typeName + '\" }');\n" +
            "        }\n" +
            "        function createNoTypedFunction() {\n" +
            "            return new Function('return { }');\n" +
            "        }\n" +
            "        function createConstructor(value) {\n" +
            "            var parsedJson = JSON.parse(value)\n" +
            "            var name = parsedJson[\"name\"]\n" +
            "            var nameSpace = parsedJson[\"nameSpace\"]\n" +
            "            var typeName = parsedJson[\"typeName\"]\n" +
            "            if (nameSpace != null) {\n" +
            "                if (typeName != null) {\n" +
            "                    window[nameSpace][name] = createFunction(typeName);\n" +
            "                } else {\n" +
            "                    window[nameSpace][name] = createNoTypedFunction();\n" +
            "                }\n" +
            "            } else {\n" +
            "                if (typeName != null) {\n" +
            "                    window[name] = createFunction(typeName);\n" +
            "                } else {\n" +
            "                    window[name] = createNoTypedFunction();\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "        function hasNameSpace(value) {\n" +
            "            return JSON.parse(value)[\"nameSpace\"] != null\n" +
            "        }\n" +
            "        function hasNotNameSpace(value) {\n" +
            "            return JSON.parse(value)[\"nameSpace\"] == null\n" +
            "        }\n" +
            "        function iterateValueEntry(values) {\n" +
            "            var baseTypes = values.filter(hasNotNameSpace)\n" +
            "            var innerTypes = values.filter(hasNameSpace)\n" +
            "            baseTypes.forEach(createConstructor)\n" +
            "            innerTypes.forEach(createConstructor)\n" +
            "        }\n" +
            "        function iterateKeyValueEntry(key, values) {\n" +
            "            iterateValueEntry(values)\n" +
            "        }\n" +
            "        for (var property in constructorsMap) {\n" +
            "            if (constructorsMap.hasOwnProperty(property)) {\n" +
            "                iterateKeyValueEntry(property, constructorsMap[property])\n" +
            "            }\n" +
            "        }\n" +
            "    },\n" +
            "    unmarshall: function (text, dynamicNamespace, callback) {\n" +
            "        // Create Jsonix context\n" +
            "        var context = new Jsonix.Context(this.mappings);\n" +
            "        // Create unmarshaller\n" +
            "        var unmarshaller = context.createUnmarshaller();\n" +
            "        var toReturn = unmarshaller.unmarshalString(text);\n" +
            "        callback(toReturn);\n" +
            "    },\n" +
            "    marshall: function (value, defaultNamespace, callback) {\n" +
            "        // Create Jsonix context\n" +
            "        var namespaces = {};\n" +
            "        var context = new Jsonix.Context(this.mappings, {\n" +
            "            namespacePrefixes: namespaces\n" +
            "        });\n" +
            "        // Create unmarshaller\n" +
            "        var marshaller = context.createMarshaller();\n" +
            "        var xmlDocument = marshaller.marshalDocument(value);\n" +
            "        var s = new XMLSerializer();\n" +
            "        var toReturn = s.serializeToString(xmlDocument);\n" +
            "        callback(toReturn);\n" +
            "    }\n" +
            "}";

    protected static native void consoleLog(String toLog)  /*-{
        console.log(toLog)
    }-*/;

    @Override
    public void gwtSetUp() {
        inject("Jsonix-all.js");
        ScriptInjector.fromString(KIE_JS).setWindow(ScriptInjector.TOP_WINDOW).inject();
        ScriptInjector.fromString(MAIN_JS).setWindow(ScriptInjector.TOP_WINDOW).inject();
        final JsPropertyMap constructorsMap = MainJs.getConstructorsMap();
        assertNotNull(constructorsMap);
        MainJs.initializeJsInteropConstructors(constructorsMap);
    }

    protected void getJSIName(JSIName retrieved, String nameSpaceUri, String localPart, String prefix, String key, String string) {
        assertNotNull(retrieved);
        assertEquals(nameSpaceUri, retrieved.getNamespaceURI());
        assertEquals(localPart, retrieved.getLocalPart());
        assertEquals(prefix, retrieved.getPrefix());
        assertEquals(key, retrieved.getKey());
        assertEquals(string, retrieved.getString());
    }

    private void inject(String fileToLoad) {
        ScriptInjector
                .fromUrl(fileToLoad)
                .setWindow(ScriptInjector.TOP_WINDOW)
                .inject();
    }

}