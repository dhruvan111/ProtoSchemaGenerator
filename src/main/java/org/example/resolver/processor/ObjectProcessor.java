package org.example.resolver.processor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;

public class ObjectProcessor {
    private static final String ANY = "google.protobuf.Any";
    private ObjectProcessor(){}
    public static int objectScan(Field field, BufferedWriter writer, int tagNumber, int cnt, SharedVariables... variables) throws IOException {
        String fieldName = field.getName();
        if (variables != null && variables.length>0){
            fieldName += variables[0].nestedCnt;
        }

        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write(ANY + " " + fieldName + "  = " + tagNumber + ";");
        writer.newLine();
        tagNumber++;

        return tagNumber;
    }
}
