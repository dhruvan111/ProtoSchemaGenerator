package org.example.resolver.generator;

import org.example.resolver.fileutilities.FileAnalyzer;
import org.example.resolver.fileutilities.FileCreator;
import org.example.resolver.processor.*;
import org.example.resolver.protoutils.ProtobufUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SchemaGenerator {

    private static final Set<Class<?>> isFileGenerated = new HashSet<>();
    private static final String IMPORT = "import";
    private static final String PROTOEXT = ".proto";
    private static final String IMPORT_ANY = "import \"google/protobuf/any.proto\";";
    private static final ConcurrentHashMap<Class<?>, Object> fileLocks = new ConcurrentHashMap<>();


    public void generateProtobufSchema(Class<?> rootClass, String outputDirectoryPath) throws IOException {

        FileCreator.makeDir(outputDirectoryPath);
        // creating .proto file for rootClass
        writeProtobufSchema(rootClass, outputDirectoryPath);
    }

    private void importWithoutCall(Class<?> fieldType, BufferedWriter writer, Class<?> clazz) throws IOException {
        if (fieldType.equals(Object.class)) {
            writer.write(IMPORT_ANY);
            writer.newLine();
            return;
        }
        String packageName = fieldType.getPackageName();
        if (fieldType == clazz) {
            return;
        }
        String importPackage = packageName.replace(".", "/");
        writer.write(IMPORT + " \"" + importPackage + "/" + fieldType.getSimpleName() + PROTOEXT + "\";");
        writer.newLine();
    }

    private void importWithCall(Class<?> fieldType, BufferedWriter writer, String outputDirectoryPath, Class<?> clazz) throws IOException {
        String packageName = fieldType.getPackageName();
        if (fieldType == clazz) {
            return;
        }
        String importPackage = packageName.replace(".", "/");
        writer.write(IMPORT + " \"" + importPackage + "/" + fieldType.getSimpleName() + PROTOEXT + "\";");
        writer.newLine();
        // recursively making .proto files for all non-primitive files
        writeProtobufSchema(fieldType, outputDirectoryPath);
    }


    private void analyzeHeaders(BufferedWriter writer, String outputDirectoryPath, Class<?> clazz, Set<Class<?>> interfaces, Set<Class<?>> superClass) throws IOException {

        Set<Class<?>> importDone;
        Set<Class<?>> fields;
        importDone = new HashSet<>();
        fields = FileAnalyzer.analyzeFields(clazz.getDeclaredFields());

        FileCreator.fileHeaders(writer, clazz.getPackageName());

        // Import for interfaces & Parent class
        for (Class<?> dependency : interfaces) {
            if (!isFileGenerated.contains(dependency)) {
                importWithCall(dependency, writer, outputDirectoryPath, clazz);
                importDone.add(dependency);
            } else if (!importDone.contains(dependency)) {
                importWithoutCall(dependency, writer, clazz);
                importDone.add(dependency);
            }
        }
        for (Class<?> dependency : superClass) {
            if (!isFileGenerated.contains(dependency)) {
                importWithCall(dependency, writer, outputDirectoryPath, clazz);
                importDone.add(dependency);
            } else if (!importDone.contains(dependency)) {
                importWithoutCall(dependency, writer, clazz);
                importDone.add(dependency);
            }
        }

        // Imports for Fields
        for (Class<?> dependency : fields) {
            if (ProtobufUtils.isPrimitiveType(dependency)) {
                continue;
            }
            if (!isFileGenerated.contains(dependency)) {
                importWithCall(dependency, writer, outputDirectoryPath, clazz);
                importDone.add(dependency);
            } else if (dependency.equals(Object.class) || !importDone.contains(dependency)) {
                importWithoutCall(dependency, writer, clazz);
                importDone.add(dependency);
            }
        }
    }

    private void writeMessage(BufferedWriter writer, Class<?> clazz, Set<Class<?>> interfaces, Set<Class<?>> superClass) throws IOException {

        int tagNumber = FileCreator.schemaDependency(writer, clazz, interfaces, superClass);

        // for every Field
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();

            // checking for any Collection Type
            if (ProtobufUtils.isPrimitiveListType(fieldType)) {
                tagNumber = ListProcessor.listScan(field, tagNumber, writer);
            }

            // Checking for Map Type
            else if (ProtobufUtils.isPrimitiveMapType(fieldType)) {
                tagNumber = MapProcessor.mapScan(field, tagNumber, writer);
            } else if (fieldType.isArray()) {
                tagNumber = ArrayProcessor.arrayScan(field, tagNumber, writer);
            }

            // Checking for Enum type
            else if (fieldType.isEnum()) {
                tagNumber = EnumProcessor.enumScan(field, fieldType, tagNumber, writer);
            } else if (fieldType.equals(Object.class)) {
                tagNumber = ObjectProcessor.objectScan(field, writer, tagNumber, 1);
            }

            // Checking for Proto Primitive types
            else if (ProtobufUtils.isPrimitiveType(fieldType)) {

                Class<?> protobufType = ProtobufUtils.getProtobufType(fieldType);
                writer.write("  " + protobufType.getSimpleName() + " " + field.getName() + " = " + (tagNumber++) + ";");
                writer.newLine();

            } else if (FileAnalyzer.checkNonPrimitive(fieldType)) {

                writer.write("  " + fieldType.getSimpleName() + " " + field.getName() + " = " + (tagNumber++) + ";");
                writer.newLine();
            }
        }
        writer.write("}");
        writer.newLine();
        writer.close();
    }


    private void writeProtobufSchema(Class<?> clazz, String outputDirectoryPath) throws IOException {

        Object fileLock = fileLocks.putIfAbsent(clazz, new Object());
        if (fileLock != null) {
            return;
        }

        try {
            // object acquired lock on file clazz
            isFileGenerated.add(clazz);
            File file = FileCreator.createFile(clazz, outputDirectoryPath);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                Set<Class<?>> interfaces;
                Set<Class<?>> superClass;
                interfaces = FileAnalyzer.analyzeImports(clazz.getInterfaces());
                superClass = FileAnalyzer.analyzeImports(new Class[]{clazz.getSuperclass()});
                // All Imports
                analyzeHeaders(writer, outputDirectoryPath, clazz, interfaces, superClass);

                // message body
                writeMessage(writer, clazz, interfaces, superClass);
            }
        }finally {
            fileLocks.remove(clazz);
        }
    }
}


