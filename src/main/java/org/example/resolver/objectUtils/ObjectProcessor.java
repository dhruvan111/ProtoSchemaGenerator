package org.example.resolver.objectUtils;

import org.example.resolver.generator.SchemaGenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;

public class ObjectProcessor {
    private static final String ANY = "google.protobuf.Any";
    private ObjectProcessor(){}
    public static int objectScan(Field field, BufferedWriter writer, int tagNumber, int cnt) throws IOException {
        String fieldName = field.getName();
        if (SchemaGenerator.factor != 0){
            fieldName += SchemaGenerator.factor;
        }

        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write(ANY + " " + fieldName + "  = " + tagNumber + ";");
        writer.newLine();
        tagNumber++;

        return tagNumber;
    }
}
