package org.example.resolver.arrayUtils;

import org.example.resolver.generator.SchemaGenerator;
import org.example.resolver.protoUtils.ProtobufUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;

public class ArrayProcessor {
    private static final String MSG = "  message ";
    private static final String ARRAY = "array";
    private static final String REPEATED = "  repeated ";

    private ArrayProcessor(){}

    private static void arrayHeader(BufferedWriter writer, String nestedListName, Field field, int tagNumber, int cnt) throws IOException {
        String currListName = nestedListName + SchemaGenerator.factor;
        String elementName = field.getName() + SchemaGenerator.factor;

        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write( REPEATED + currListName + " " + elementName + " = " + (tagNumber) + ";");
        writer.newLine();

        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write(MSG + currListName + " {");
        writer.newLine();
        writer.newLine();
    }

    private static int createArray(Field field, Class<?> argument, int tagNumber, int cnt, BufferedWriter writer) throws IOException {
        if (argument.isArray()){
            // nested array
            String nestedListName = SchemaGenerator.capitalize(field.getName()) + ARRAY;
            cnt++;
            SchemaGenerator.factor++;
            argument = argument.getComponentType();
            arrayHeader(writer, nestedListName, field, tagNumber, cnt);

            cnt = createArray(field, argument, tagNumber, cnt, writer);
            writer.write("  ".repeat(Math.max(0, cnt)));
            writer.write("  }");
            writer.newLine();
            return cnt-1;
        }
        else {
            String innerClassName = argument.getSimpleName();
            if (ProtobufUtils.isPrimitiveType(argument)) {
                innerClassName = ProtobufUtils.getProtobufType(argument).getSimpleName();
            }
            SchemaGenerator.factor++;
            writer.write("  ".repeat(Math.max(0, cnt)));
            writer.write(REPEATED + innerClassName + " " + field.getName() + " = " + tagNumber + ";");
            writer.newLine();
            return cnt;
        }
    }

    public static int arrayScan(Field field, int tagNumber, BufferedWriter writer) throws IOException {
        Class<?> fieldType = field.getType();
        Class<?> componentType = fieldType.getComponentType();
        createArray(field, componentType, tagNumber, 0, writer);
        tagNumber++;
        return tagNumber;
    }
}
