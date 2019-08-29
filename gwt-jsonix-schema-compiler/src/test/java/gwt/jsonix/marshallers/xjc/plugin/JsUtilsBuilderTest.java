package gwt.jsonix.marshallers.xjc.plugin;

import java.io.IOException;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import gwt.jsonix.marshallers.xjc.plugin.builders.JsUtilsBuilder;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class JsUtilsBuilderTest extends AbstractBuilderTest {


    @Test
    public void generateJsUtilsClass() throws JClassAlreadyExistsException, IOException {
        final JDefinedClass retrieved = JsUtilsBuilder.generateJsUtilsClass(jCodeModel);
        assertNotNull(retrieved);
        printJDefinedClass(retrieved);
    }

}
