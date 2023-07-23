package org.example.resolver.processor;

import org.example.resolver.fileutilities.FileCreator;
import org.example.resolver.protoutils.ProtobufUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;

public class ArrayProcessor{
    private static final String MESSAGE = "  message ";
    private static final String ARRAY = "array";
    private static final String REPEATED = "  repeated ";

    private ArrayProcessor(){}

    private static void arrayHeader(BufferedWriter writer, String nestedListName, Field field, SharedVariables variables, int tagNumber, int cnt) throws IOException {
        String currListName = nestedListName + variables.nestedCnt;
        String elementName = field.getName() + variables.nestedCnt;

        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write( REPEATED + currListName + " " + elementName + " = " + (tagNumber) + ";");
        writer.newLine();

        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write(MESSAGE + currListName + " {");
        writer.newLine();
        writer.newLine();
    }

    private static int createArray(Field field, Class<?> argument, SharedVariables variables, int tagNumber, int cnt, BufferedWriter writer) throws IOException {
        if (argument.isArray()){
            // nested array
            String nestedListName = FileCreator.capitalize(field.getName()) + ARRAY;
            cnt++;
            variables.nestedCnt++;
            argument = argument.getComponentType();
            arrayHeader(writer, nestedListName, field, variables, tagNumber, cnt);

            cnt = createArray(field, argument, variables, tagNumber, cnt, writer);
            writer.write("  ".repeat(Math.max(0, cnt)));
            writer.write("  }");
            writer.newLine();
            return cnt-1;
        }
        else {
            String innerClassName = argument.getSimpleName();
            if (ProtobufUtils.isPrimitiveType(argument)) {
                innerClassName = ProtobufUtils.getProtoPrimitiveType(argument).getSimpleName();
            }
            variables.nestedCnt++;
            writer.write("  ".repeat(Math.max(0, cnt)));
            writer.write(REPEATED + innerClassName + " " + field.getName() + " = " + tagNumber + ";");
            writer.newLine();
            return cnt;
        }
    }

    public static int arrayScan(Field field, int tagNumber, BufferedWriter writer) throws IOException {
        SharedVariables variables = new SharedVariables();
        variables.nestedCnt = 0;

        Class<?> fieldType = field.getType();
        Class<?> componentType = fieldType.getComponentType();

        createArray(field, componentType, variables, tagNumber, 0, writer);
        tagNumber++;
        return tagNumber;
    }
}
