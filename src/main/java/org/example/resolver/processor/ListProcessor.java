package org.example.resolver.processor;

import org.example.resolver.generator.SchemaGenerator;
import org.example.resolver.protoutils.ProtobufUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ListProcessor{
    private static final String REPEATED = "  repeated ";
    private static final String ANY = "google.protobuf.Any";
    private static final String LIST = "List";
    private static final String MSG = "  message ";
    private ListProcessor(){}

    private static void listHeader(BufferedWriter writer,String nestedListName, Field field,SharedVariables variables, int cnt, int tagNumber) throws IOException {
        String currListName = nestedListName + variables.nestedCnt;
        String elementName = field.getName() + variables.nestedCnt;

        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write( REPEATED + currListName + " " + elementName + " = " + (tagNumber) + ";");
        writer.newLine();

        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write(MSG + currListName + " {");
        writer.newLine();
        writer.newLine();
    }

    public static int listScan(Field field, int tagNumber, BufferedWriter writer) throws IOException {
        Type genericType = field.getGenericType();
        SharedVariables variables = new SharedVariables();
        variables.nestedCnt = 0;

        if (genericType instanceof ParameterizedType parameterizedType) {

            Type[] typeArguments = (parameterizedType).getActualTypeArguments();
            createList(writer, typeArguments,field, variables, tagNumber, 0);
            tagNumber++;
            writer.newLine();
        }
        return tagNumber;
    }

    public static int createList(BufferedWriter writer, Type[] typeArguments, Field field, SharedVariables variables, int tagNumber, int cnt) throws IOException {

        if (typeArguments.length>0 && typeArguments[0] instanceof ParameterizedType parameterizedType){

            String nestedListName = SchemaGenerator.capitalize(field.getName()) + LIST;
            Type[] typeArguments2 = (parameterizedType).getActualTypeArguments();
            listHeader(writer, nestedListName, field, variables, cnt, tagNumber);

            cnt++;
            variables.nestedCnt++;
            if (typeArguments2.length == 1){
                // Nested List
                cnt = createList(writer, typeArguments2, field, variables, 1, cnt);
            }
            else {
                // Nested Map
                if (MapProcessor.checkSimpleMap(typeArguments2)){
                    cnt = MapProcessor.simpleMap(typeArguments2, field, writer,variables, 1, cnt);
                }
                else {
                    cnt = MapProcessor.complexMap(typeArguments2, field, writer,variables, 1, cnt);
                }
            }
            writer.write("  ".repeat(Math.max(0, cnt)));
            writer.write("  }");
            writer.newLine();
            return cnt-1;
        }
        else if (typeArguments.length>0 && typeArguments[0] instanceof Class<?> innerClass){

            String className = innerClass.getSimpleName();
            if (ProtobufUtils.isPrimitiveType(innerClass)){
                className = ProtobufUtils.getProtobufType(innerClass).getSimpleName();
            }
            else if (innerClass.equals(Object.class)){
                className = ANY;
            }
            String elementName = field.getName() + variables.nestedCnt;
            variables.nestedCnt++;
            writer.write("  ".repeat(Math.max(0, cnt)));
            writer.write(REPEATED + className + " " + elementName + " = " + (tagNumber) + ";");
            writer.newLine();
            return cnt;
        }
        return 0;
    }
}
