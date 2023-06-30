package org.example.resolver.processor;

import org.example.resolver.protoutils.ProtobufUtils;

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

    private static void complexMapHeader(BufferedWriter writer, Field field,SharedVariables variables, int cnt, int tagNumber) throws IOException {
        String schemaName = field.getName() + ENTRY + variables.nestedCnt;
        String mapName = field.getName() + MAP + variables.nestedCnt;

        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write(REPEATED + schemaName + " " + mapName + " = " + tagNumber + ";");
        writer.newLine();

        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write(MSG + schemaName + " {");
        writer.newLine();
        writer.newLine();
    }

    private static void simpleMapHeader(BufferedWriter writer, Field field, Class<?> firstArgClass,SharedVariables variables, int cnt, int tagNumber) throws IOException {
        String secondClass = field.getName() + ENTRY + variables.nestedCnt;
        String mapName = field.getName() + MAP + variables.nestedCnt;

        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write(  "  " + MAP + "<"  + firstArgClass.getSimpleName() + "," + secondClass + "> " + mapName + " = " + tagNumber +  ";");
        writer.newLine();

        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write(MSG + secondClass + "{");
        writer.newLine();
    }

    private static int firstArgComplexMap(Type firstArg, BufferedWriter writer, Field field, SharedVariables variables, int cnt) throws IOException {
        if (firstArg instanceof ParameterizedType parameterizedType) {
            Type[] typeArguments2 = (parameterizedType).getActualTypeArguments();
            if (typeArguments2.length == 1) {
                // List Type
                cnt = ListProcessor.createList(writer, typeArguments2, field, variables, 1, cnt);
            } else {
                // Map Type
                if (checkSimpleMap(typeArguments2)) {
                    cnt = simpleMap(typeArguments2, field, writer,variables, 1, cnt);
                } else {
                    cnt = complexMap(typeArguments2, field, writer, variables, 1, cnt);
                }
            }
        } else if (firstArg instanceof Class<?> innerClass) {
            if (innerClass.equals(Object.class)) {
                ObjectProcessor.objectScan(field, writer, 1, cnt, variables);
                variables.nestedCnt++;
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
        return cnt;
    }

    private static int secondArgComplexMap(Type secondArg, BufferedWriter writer, Field field, SharedVariables variables, int cnt) throws IOException {
        if (secondArg instanceof ParameterizedType parameterizedType) {
            Type[] typeArguments2 = (parameterizedType).getActualTypeArguments();
            if (typeArguments2.length == 1) {
                // List Type
                cnt = ListProcessor.createList(writer, typeArguments2, field,variables, 2, cnt);
            } else {
                // Map Type
                if (checkSimpleMap(typeArguments2)) {
                    cnt = simpleMap(typeArguments2, field, writer,variables,  2, cnt);
                } else {
                    cnt = complexMap(typeArguments2, field, writer,variables, 2, cnt);
                }
            }
        } else if (secondArg instanceof Class<?> innerClass) {
            if (innerClass.equals(Object.class)) {
                ObjectProcessor.objectScan(field, writer, 1, cnt, variables);
                variables.nestedCnt++;
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
        return cnt;
    }


    public static int complexMap(Type[] typeArguments, Field field, BufferedWriter writer,SharedVariables variables, int tagNumber, int cnt) throws IOException {
        Type firstArg = typeArguments[0];
        Type secondArg = typeArguments[1];

        complexMapHeader(writer, field,variables, cnt, tagNumber);
        cnt++;
        variables.nestedCnt++;

        cnt = firstArgComplexMap(firstArg, writer, field, variables, cnt);

        cnt = secondArgComplexMap(secondArg, writer, field, variables, cnt);

        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write("  }");
        writer.newLine();
        return cnt - 1;
    }

    private static int parameterizedArg(ParameterizedType parameterizedType, Class<?> firstArgClass, BufferedWriter writer, Field field,
                                        SharedVariables variables, int tagNumber, int cnt) throws IOException {

        Type[] typeArguments2 = (parameterizedType).getActualTypeArguments();
        simpleMapHeader(writer, field, firstArgClass, variables, cnt, tagNumber);

        cnt++;
        variables.nestedCnt++;
        if (typeArguments2.length == 1){
            // List Type
            cnt = ListProcessor.createList(writer, typeArguments2, field,variables, tagNumber, cnt);
        }
        else {
            // Map Type
            if (checkSimpleMap(typeArguments2)) {
                cnt = simpleMap(typeArguments2, field, writer,variables, tagNumber, cnt);
            }
            else{
                cnt = complexMap(typeArguments2, field, writer, variables, tagNumber, cnt);
            }
        }
        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write("  }");
        writer.newLine();
        return cnt-1;
    }

    private static int nonParameterizedArg(Class<?> secondArgClass, Class<?> firstArgClass, BufferedWriter writer, Field field,
                                           SharedVariables variables, int tagNumber, int cnt) throws IOException {

        if (secondArgClass.equals(Object.class)){
            simpleMapHeader(writer, field, firstArgClass, variables, cnt, tagNumber);
            cnt++;
            variables.nestedCnt++;
            ObjectProcessor.objectScan(field, writer, tagNumber, cnt, variables);
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

    public static int simpleMap(Type[] typeArguments, Field field, BufferedWriter writer,SharedVariables variables, int tagNumber, int cnt) throws IOException {
        Type firstArg = typeArguments[0];
        Type secondArg = typeArguments[1];

        // here firstArg is primitive for Map
        Class<?> firstArgClass = (Class<?>) firstArg;
        firstArgClass = ProtobufUtils.getProtoKeyType(firstArgClass);


        if (secondArg instanceof ParameterizedType parameterizedType){
            return parameterizedArg(parameterizedType, firstArgClass, writer, field, variables, tagNumber, cnt);
        }

        else if (secondArg instanceof Class<?> secondArgClass){
            return nonParameterizedArg(secondArgClass, firstArgClass, writer, field, variables, tagNumber, cnt);
        }
        return 0;
    }

    public static int mapScan(Field field, int tagNumber, BufferedWriter writer) throws IOException {
        Type genericType = field.getGenericType();
        SharedVariables variables = new SharedVariables();
        variables.nestedCnt = 0;

        if (genericType instanceof ParameterizedType parameterizedType){
            Type[] typeArguments = (parameterizedType).getActualTypeArguments();
            if (checkSimpleMap(typeArguments)){
                simpleMap(typeArguments, field, writer,variables, tagNumber, 0);
            }
            else {
                complexMap(typeArguments, field, writer,variables, tagNumber, 0);
            }
            tagNumber++;
            writer.newLine();
        }
        return tagNumber;
    }

}
