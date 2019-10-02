package gwt.jsonix.marshallers.xjc.plugin.builders;

import java.io.IOException;
import java.util.Map;

import javax.xml.namespace.QName;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;
import gwt.jsonix.marshallers.xjc.plugin.AbstractBuilderTest;
import jsinterop.base.JsArrayLike;
import org.junit.Test;

import static gwt.jsonix.marshallers.xjc.plugin.builders.JsUtilsBuilder.FROM_ATTRIBUTES_MAP_METHOD_BODY;
import static gwt.jsonix.marshallers.xjc.plugin.builders.JsUtilsBuilder.GENERIC_EXTEND_TYPE_NAME;
import static gwt.jsonix.marshallers.xjc.plugin.builders.JsUtilsBuilder.GENERIC_TYPE_NAME;
import static gwt.jsonix.marshallers.xjc.plugin.builders.JsUtilsBuilder.PUBLIC_STATIC_MODS;
import static gwt.jsonix.marshallers.xjc.plugin.builders.JsUtilsBuilder.PUBLIC_STATIC_NATIVE_MODS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JsUtilsBuilderTest extends AbstractBuilderTest {

    @Test
    public void generateJsUtilsClass() throws JClassAlreadyExistsException, IOException {
        final JDefinedClass retrieved = JsUtilsBuilder.generateJsUtilsClass(jCodeModel, "fake.testing");
        assertNotNull(retrieved);
        printJDefinedClass(retrieved);
    }

    @Test
    public void addJavaFromAttributesMapMethod() {
        final JMethod retrieved = JsUtilsBuilder.addJavaFromAttributesMapMethod(jCodeModel, jDefinedClass);
        assertNotNull(retrieved);
        assertEquals(PUBLIC_STATIC_MODS, retrieved.mods().getValue());
        assertEquals("fromAttributesMap", retrieved.name());
        commonVerifyQNameStringNarrowedMapClass((JClass) retrieved.params().get(0).type());
        assertEquals(GENERIC_TYPE_NAME, retrieved.type().binaryName());
        assertEquals(GENERIC_TYPE_NAME, retrieved.typeParams()[0].binaryName());
        final JBlock retrievedBody = retrieved.body();
        assertEquals("toReturn", ((JVar) retrievedBody.getContents().get(0)).name());
        assertEquals(GENERIC_TYPE_NAME, ((JVar) retrievedBody.getContents().get(0)).type().binaryName());
        // TODO {gcardosi} to complete
    }

    @Test
    public void getGenerifiedJMethodByClass() {
        final JMethod retrieved = JsUtilsBuilder.getGenerifiedJMethod(jDefinedClass, String.class, "testingMethod");
        assertNotNull(retrieved);
        assertEquals(PUBLIC_STATIC_MODS, retrieved.mods().getValue());
        assertEquals("testingMethod", retrieved.name());
        assertEquals(String.class.getCanonicalName(), retrieved.type().binaryName());
        assertEquals(GENERIC_TYPE_NAME, retrieved.typeParams()[0].binaryName());
    }

    @Test
    public void getGenerifiedJMethodByJClass() {
        final JMethod retrieved = JsUtilsBuilder.getGenerifiedJMethod(jDefinedClass, stringClass, "testingMethod");
        assertNotNull(retrieved);
        assertEquals(PUBLIC_STATIC_MODS, retrieved.mods().getValue());
        assertEquals("testingMethod", retrieved.name());
        assertEquals(stringClass, retrieved.type());
        assertEquals(GENERIC_TYPE_NAME, retrieved.typeParams()[0].binaryName());
    }

    @Test
    public void getJMethodByClass() {
        final JMethod retrieved = JsUtilsBuilder.getJMethod(jDefinedClass, String.class, "testingMethod");
        assertNotNull(retrieved);
        assertEquals(PUBLIC_STATIC_MODS, retrieved.mods().getValue());
        assertEquals("testingMethod", retrieved.name());
        assertEquals(String.class.getCanonicalName(), retrieved.type().binaryName());
    }

    @Test
    public void getJMethodByJClass() {
        final JMethod retrieved = JsUtilsBuilder.getJMethod(jDefinedClass, stringClass, "testingMethod");
        assertNotNull(retrieved);
        assertEquals(PUBLIC_STATIC_MODS, retrieved.mods().getValue());
        assertEquals("testingMethod", retrieved.name());
        assertEquals(stringClass, retrieved.type());
    }

    @Test
    public void getNativeJMethodByClass() {
        final JMethod retrieved = JsUtilsBuilder.getNativeJMethod(jDefinedClass, String.class, "testingMethod");
        assertNotNull(retrieved);
        assertEquals(PUBLIC_STATIC_NATIVE_MODS, retrieved.mods().getValue());
        assertEquals("testingMethod", retrieved.name());
        assertEquals(String.class.getCanonicalName(), retrieved.type().binaryName());
    }

    @Test
    public void getNativeJMethodByJClass() {
        final JMethod retrieved = JsUtilsBuilder.getNativeJMethod(jDefinedClass, stringClass, "testingMethod");
        assertNotNull(retrieved);
        assertEquals(PUBLIC_STATIC_NATIVE_MODS, retrieved.mods().getValue());
        assertEquals("testingMethod", retrieved.name());
        assertEquals(stringClass, retrieved.type());
    }

    @Test
    public void getJSArrayNarrowedJVar() {
        final JVar retrieved = JsUtilsBuilder.getJSArrayNarrowedJVar(jCodeModel, setJMethod);
        assertNotNull(retrieved);
        assertEquals(retrieved, setJMethod.params().get(0));
        commonVerifyJsArrayNarrowedClass((JClass) retrieved.type());
    }

    @Test
    public void getGenericT() {
        final JClass retrieved = JsUtilsBuilder.getGenericT(jCodeModel);
        assertNotNull(retrieved);
        assertEquals(GENERIC_TYPE_NAME, retrieved.binaryName());
    }

    @Test
    public void getGenericTExtends() {
        final JClass retrieved = JsUtilsBuilder.getGenericTExtends(jCodeModel);
        assertNotNull(retrieved);
        assertEquals(GENERIC_EXTEND_TYPE_NAME, retrieved.binaryName());
    }

    @Test
    public void getJsArrayNarrowedClass() {
        final JClass retrieved = JsUtilsBuilder.getJsArrayNarrowedClass(jCodeModel);
        commonVerifyJsArrayNarrowedClass(retrieved);
    }

    @Test
    public void getQNameStringNarrowedMapClass() {
        final JClass retrieved = JsUtilsBuilder.getQNameStringNarrowedMapClass(jCodeModel);
        commonVerifyQNameStringNarrowedMapClass(retrieved);
    }

    private void commonVerifyQNameStringNarrowedMapClass(final JClass retrieved) {
        assertNotNull(retrieved);
        assertEquals(Map.class.getCanonicalName(), retrieved.erasure().binaryName());
        assertEquals(QName.class.getCanonicalName(), retrieved.getTypeParameters().get(0).binaryName());
        assertEquals(String.class.getCanonicalName(), retrieved.getTypeParameters().get(1).binaryName());
    }

    private void commonVerifyJsArrayNarrowedClass(JClass retrieved) {
        assertNotNull(retrieved);
        assertEquals(JsArrayLike.class.getCanonicalName(), retrieved.erasure().binaryName());
        assertEquals(GENERIC_TYPE_NAME, retrieved.getTypeParameters().get(0).binaryName());
    }
}
