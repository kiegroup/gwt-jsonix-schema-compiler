package gwt.jsonix.marshallers.xjc.plugin.util;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JType;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.type.ClassOrInterfaceType;

public class JavaTypeParser {

    private final TypeToJTypeConvertingVisitor typeToJTypeConvertingVisitor;

    public JavaTypeParser() {
        this(Collections.emptyMap());
    }

    public JavaTypeParser(Map<String, JDefinedClass> definedClassesMap) {
        this.typeToJTypeConvertingVisitor = new TypeToJTypeConvertingVisitor(definedClassesMap);
    }

    public JClass parseClass(String _class, JCodeModel codeModel) {
        JType type = parseType(_class, codeModel);
        if (type instanceof JClass) {
            return (JClass) type;
        } else {
            throw new IllegalArgumentException("Type [" + _class
                                                       + "] is not a class.");
        }
    }

    private JType parseType(String type, JCodeModel codeModel) {
        final String text = "public class Ignored extends " + type + " {}";
        try {
            CompilationUnit compilationUnit = JavaParser.parse(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)), "UTF-8");
            final List<TypeDeclaration> typeDeclarations = compilationUnit.getTypes();
            final TypeDeclaration typeDeclaration = typeDeclarations.get(0);
            final ClassOrInterfaceDeclaration classDeclaration = (ClassOrInterfaceDeclaration) typeDeclaration;
            final List<ClassOrInterfaceType> _extended = classDeclaration.getExtends();
            final ClassOrInterfaceType classOrInterfaceType = _extended.get(0);

            return classOrInterfaceType.accept(this.typeToJTypeConvertingVisitor, codeModel);
        } catch (ParseException pex) {
            throw new IllegalArgumentException("Could not parse the type definition [" + type + "].", pex);
        }
    }
}