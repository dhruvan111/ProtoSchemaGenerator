package org.example.resolver.enumUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;

public class EnumProcessor {
    private static final String ENUM = "enum";
    private EnumProcessor(){}

    public static int enumScan(Field field, Class<?> fieldType, int tagNumber, BufferedWriter writer) throws IOException {

        String fieldName = field.getName();
        String enumName = fieldType.getSimpleName();
        writer.write("  " + enumName + " " + fieldName + " = " + tagNumber + ";");
        writer.newLine();
        tagNumber++;

        writer.write("  " + ENUM + " " + enumName + "{");
        writer.newLine();

        Object[] enumConstants = fieldType.getEnumConstants();
        int enumTagNo = 0;
        for (Object enumConstant:enumConstants){
            writer.write("    ");
            writer.write(enumConstant.toString() + " = " + enumTagNo + ";");
            enumTagNo++;
            writer.newLine();
        }
        writer.write("  }");
        writer.newLine();
        writer.newLine();

        return tagNumber;
    }

}
