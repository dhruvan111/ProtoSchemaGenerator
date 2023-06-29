package org.example.resolver.mapUtils;

import org.example.resolver.generator.SchemaGenerator;
import org.example.resolver.listUtils.ListProcessor;
import org.example.resolver.objectUtils.ObjectProcessor;
import org.example.resolver.protoUtils.ProtobufUtils;

import javax.xml.validation.Schema;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class MapProcessor {
    private static final String MAP = "map";
    private static final String REPEATED = "  repeated ";
    private static final String MSG = "  message ";
    private static final String ENTRY = "Entry";
    private static final String KEY = " key = 1;";
    private static final String VAL = " value = 2;";

    private MapProcessor(){}

    public static boolean checkSimpleMap(Type[] typeArguments){
        if (typeArguments[0] instanceof Class<?> nestedClass){
            return ProtobufUtils.isPrimitiveKeyType(nestedClass);
        }
        return false;
    }

    private static void complexMapHeader(BufferedWriter writer, Field field, int cnt, int tagNumber) throws IOException {
        String schemaName = field.getName() + ENTRY + SchemaGenerator.factor;
        String mapName = field.getName() + MAP + SchemaGenerator.factor;

        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write(REPEATED + schemaName + " " + mapName + " = " + tagNumber + ";");
        writer.newLine();

        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write(MSG + schemaName + " {");
        writer.newLine();
        writer.newLine();
    }

    private static void simpleMapHeader(BufferedWriter writer, Field field, Class<?> firstArgClass, int cnt, int tagNumber) throws IOException {
        String secondClass = field.getName() + ENTRY + SchemaGenerator.factor;
        String mapName = field.getName() + MAP + SchemaGenerator.factor;

        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write(  "  " + MAP + "<"  + firstArgClass.getSimpleName() + "," + secondClass + "> " + mapName + " = " + tagNumber +  ";");
        writer.newLine();

        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write(MSG + secondClass + "{");
        writer.newLine();
    }

    public static int complexMap(Type[] typeArguments, Field field, BufferedWriter writer, int tagNumber, int cnt) throws IOException {
        Type firstArg = typeArguments[0];
        Type secondArg = typeArguments[1];

        complexMapHeader(writer, field, cnt, tagNumber);
        cnt++;
        SchemaGenerator.factor++;
        if (firstArg instanceof ParameterizedType) {
            Type[] typeArguments2 = ((ParameterizedType) firstArg).getActualTypeArguments();
            if (typeArguments2.length == 1) {
                // List Type
                cnt = ListProcessor.createList(writer, typeArguments2, field, 1, cnt);
            } else {
                // Map Type
                if (checkSimpleMap(typeArguments2)) {
                    cnt = simpleMap(typeArguments2, field, writer, 1, cnt);
                } else {
                    cnt = complexMap(typeArguments2, field, writer, 1, cnt);
                }
            }
        } else if (firstArg instanceof Class<?> innerClass) {
            if (innerClass.equals(Object.class)) {
                ObjectProcessor.objectScan(field, writer, 1, cnt);
                SchemaGenerator.factor++;
            } else {
                String keyName = innerClass.getSimpleName();
                if (ProtobufUtils.isPrimitiveMapType(innerClass)) {
                    keyName = ProtobufUtils.getProtoMapType().getSimpleName();
                }
                writer.write("  ".repeat(Math.max(0, cnt)));
                writer.write("  " + keyName + KEY);
                writer.newLine();
            }
        }

        // checking for second Arg
        if (secondArg instanceof ParameterizedType) {
            Type[] typeArguments2 = ((ParameterizedType) secondArg).getActualTypeArguments();
            if (typeArguments2.length == 1) {
                // List Type
                cnt = ListProcessor.createList(writer, typeArguments2, field, 2, cnt);
            } else {
                // Map Type
                if (checkSimpleMap(typeArguments2)) {
                    cnt = simpleMap(typeArguments2, field, writer, 2, cnt);
                } else {
                    cnt = complexMap(typeArguments2, field, writer, 2, cnt);
                }
            }
        } else if (secondArg instanceof Class<?> innerClass) {
            if (innerClass.equals(Object.class)) {
                ObjectProcessor.objectScan(field, writer, 1, cnt);
                SchemaGenerator.factor++;
            } else {
                String keyName = innerClass.getSimpleName();
                if (ProtobufUtils.isPrimitiveMapType(innerClass)) {
                    keyName = ProtobufUtils.getProtoMapType().getSimpleName();
                }
                writer.write("  ".repeat(Math.max(0, cnt)));
                writer.write("  " + keyName + VAL);
                writer.newLine();
            }
        }
        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write("  }");
        writer.newLine();
        return cnt - 1;
    }

    public static int simpleMap(Type[] typeArguments, Field field, BufferedWriter writer, int tagNumber, int cnt) throws IOException {
        Type firstArg = typeArguments[0];
        Type secondArg = typeArguments[1];

        Class<?> firstArgClass = (Class<?>) firstArg;
        firstArgClass = ProtobufUtils.getProtoKeyType(firstArgClass);

        // here firstArg is primitive for Map
        if (secondArg instanceof ParameterizedType){
            Type[] typeArguments2 = ((ParameterizedType) secondArg).getActualTypeArguments();
            simpleMapHeader(writer, field, firstArgClass, cnt, tagNumber);

            cnt++;
            SchemaGenerator.factor++;
            if (typeArguments2.length == 1){
                // List Type
                cnt = ListProcessor.createList(writer, typeArguments2, field, tagNumber, cnt);
            }
            else {
                // Map Type
                if (checkSimpleMap(typeArguments2)) {
                    cnt = simpleMap(typeArguments2, field, writer, tagNumber, cnt);
                }
                else{
                    cnt = complexMap(typeArguments2, field, writer, tagNumber, cnt);
                }
            }
            writer.write("  ".repeat(Math.max(0, cnt)));
            writer.write("  }");
            writer.newLine();
            return cnt-1;
        }
        else if (secondArg instanceof Class<?> secondArgClass){

            if (secondArgClass.equals(Object.class)){
                simpleMapHeader(writer, field, firstArgClass, cnt, tagNumber);
                cnt++;
                SchemaGenerator.factor++;
                ObjectProcessor.objectScan(field, writer, tagNumber, cnt);
                writer.write("  ".repeat(Math.max(0, cnt)));
                writer.write("  }");
                writer.newLine();
                return cnt-1;
            }
            else {
                String mapName = field.getName();
                writer.write("  ".repeat(Math.max(0, cnt)));
                writer.write(   "  " + MAP + "<" + firstArgClass.getSimpleName() + "," + secondArgClass.getSimpleName() + "> " + mapName + " = " + tagNumber +  ";");
                writer.newLine();
            }
            return cnt;
        }
        return 0;
    }

    public static int mapScan(Field field, int tagNumber, BufferedWriter writer) throws IOException {
        Type genericType = field.getGenericType();

        if (genericType instanceof ParameterizedType){
            Type[] typeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
            if (checkSimpleMap(typeArguments)){
                simpleMap(typeArguments, field, writer, tagNumber, 0);
            }
            else {
                complexMap(typeArguments, field, writer, tagNumber, 0);
            }
            tagNumber++;
            writer.newLine();
        }
        return tagNumber;
    }

}
