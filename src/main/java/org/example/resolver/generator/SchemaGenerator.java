package org.example.resolver.generator;

import org.example.resolver.arrayUtils.ArrayProcessor;
import org.example.resolver.enumUtils.EnumProcessor;
import org.example.resolver.fileScan.FileCreator;
import org.example.resolver.fileScan.FileScanner;
import org.example.resolver.listUtils.ListProcessor;
import org.example.resolver.mapUtils.MapProcessor;
import org.example.resolver.objectUtils.ObjectProcessor;
import org.example.resolver.protoUtils.ProtobufUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class SchemaGenerator {

    private Set<Class<?>> schemaGen;
    private static final String IMPORT = "import";
    private static final String PROTOEXT = ".proto";
    private static final String PROTOVERSION = "syntax = \"proto3\";";
    private static final String IMPORT_ANY = "import \"google/protobuf/any.proto\";";
    private static final String JAVA_PKG = "option java_package = ";
    private static final String MULTIPLE_FILES_OPN = "option java_multiple_files = true;";
    private static final String FILE_CREATE_ERR = "Unable to create file at specified path.";

    public static int factor;
    public void generateProtobufSchema(Class<?> rootClass, String outputDirectoryPath) throws IOException {

        schemaGen = new HashSet<>();
        makeDir(outputDirectoryPath);
        // creating .proto file for rootClass
        writeProtobufSchema(rootClass, outputDirectoryPath);
    }

    private void makeDir(String outputDirectoryPath) throws IOException {

        File outputDirectory = new File(outputDirectoryPath);
        if (!outputDirectory.exists()) {
            boolean dirCreated = outputDirectory.mkdirs();
            if (!dirCreated){
                throw new IOException(FILE_CREATE_ERR);
            }
        }
    }

    private void importWithoutCall(Class<?> fieldType, BufferedWriter writer, Class<?> clazz) throws IOException {
        if (fieldType.equals(Object.class)){
            writer.write(IMPORT_ANY);
            writer.newLine();
            return;
        }
        String packageName = fieldType.getPackageName();
        if (fieldType == clazz){
            return;
        }
        String importPackage = packageName.replace(".", "/");
        writer.write( IMPORT + " \"" + importPackage + "/" + fieldType.getSimpleName() +  PROTOEXT + "\";");
        writer.newLine();
    }

    private void importWithCall(Class<?> fieldType,BufferedWriter writer, String outputDirectoryPath, Class<?> clazz) throws IOException {
        String packageName = fieldType.getPackageName();
        if (fieldType == clazz){
            return;
        }
        String importPackage = packageName.replace(".", "/");
        writer.write( IMPORT + " \"" + importPackage + "/" + fieldType.getSimpleName() +  PROTOEXT + "\";");
        writer.newLine();
        // recursively making .proto files for all non-primitive files
        writeProtobufSchema(fieldType, outputDirectoryPath);
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private void hardCodeHeaders(BufferedWriter writer,String packageName) throws IOException {
        writer.write(PROTOVERSION);
        writer.newLine();
        writer.newLine();

        writer.write(JAVA_PKG + "\"" + packageName + "\";");
        writer.newLine();
        writer.newLine();

        writer.write(MULTIPLE_FILES_OPN);
        writer.newLine();
        writer.newLine();
    }


    private void writeHeaders(BufferedWriter writer, String outputDirectoryPath, Class<?> clazz, Set<Class<?>> interfaces, Set<Class<?>> superClass) throws IOException {

        Set<Class<?>> importDone, fields;
        importDone = new HashSet<>();
        fields = FileScanner.analyzeFields(clazz.getDeclaredFields());

        hardCodeHeaders(writer, clazz.getPackageName());

        // Import for interfaces & Parent class
        for (Class<?> dependency:interfaces){
            if (!schemaGen.contains(dependency)){
                importWithCall(dependency, writer, outputDirectoryPath, clazz);
                importDone.add(dependency);
            }
            else if (!importDone.contains(dependency)){
                importWithoutCall(dependency, writer, clazz);
                importDone.add(dependency);
            }
        }
        for (Class<?> dependency:superClass){
            if (!schemaGen.contains(dependency)){
                importWithCall(dependency, writer, outputDirectoryPath, clazz);
                importDone.add(dependency);
            }
            else if (!importDone.contains(dependency)){
                importWithoutCall(dependency, writer, clazz);
                importDone.add(dependency);
            }
        }

        // Imports for Fields
        for (Class<?> dependency:fields){
            if (ProtobufUtils.isPrimitiveType(dependency)){
                continue;
            }
            if (dependency.equals(Object.class)){
                importWithoutCall(dependency, writer, clazz);
                importDone.add(dependency);
            }
            else if (!schemaGen.contains(dependency)){
                importWithCall(dependency, writer, outputDirectoryPath, clazz);
                importDone.add(dependency);
            }
            else if (!importDone.contains(dependency)){
                importWithoutCall(dependency, writer, clazz);
                importDone.add(dependency);
            }
        }
    }

    private int schemaDependency(BufferedWriter writer, Class<?> clazz, Set<Class<?>> interfaces, Set<Class<?>> superClass) throws IOException {

        writer.newLine();
        writer.write("message " + clazz.getSimpleName() + " {");
        writer.newLine();

        int tagNumber = 1;

        for (Class<?> dependency: interfaces){
            String dependencyName = dependency.getSimpleName() + "Implementation";
            writer.write("  " + dependency.getSimpleName() + " " + dependencyName + " = " + (tagNumber++) + ";");
            writer.newLine();
        }

        for (Class<?> dependency: superClass){
            String dependencyName = dependency.getSimpleName() + "Instance";
            writer.write("  " + dependency.getSimpleName() + " " + dependencyName + " = " + (tagNumber++) + ";");
            writer.newLine();
        }
        return tagNumber;
    }

    private void writeMessage(BufferedWriter writer, Class<?> clazz, Set<Class<?>> interfaces, Set<Class<?>> superClass) throws IOException {

        int tagNumber = schemaDependency(writer, clazz, interfaces, superClass);

        // for every Field
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            factor = 0;

            // checking for any Collection Type
            if (ProtobufUtils.isPrimitiveListType(fieldType)){
                tagNumber = ListProcessor.listScan(field, tagNumber, writer);
            }

            // Checking for Map Type
            else if (ProtobufUtils.isPrimitiveMapType(fieldType)){
                tagNumber = MapProcessor.mapScan(field, tagNumber, writer);
            }

            else if (fieldType.isArray()){
                tagNumber = ArrayProcessor.arrayScan(field, tagNumber, writer);
            }

            // Checking for Enum type
            else if (fieldType.isEnum()){
                tagNumber = EnumProcessor.enumScan(field, fieldType, tagNumber, writer);
            }

            else if (fieldType.equals(Object.class)){
                tagNumber = ObjectProcessor.objectScan(field, writer, tagNumber, 1);
            }

            // Checking for Proto Primitive types
            else if (ProtobufUtils.isPrimitiveType(fieldType)) {

                Class<?> protobufType = ProtobufUtils.getProtobufType(fieldType);
                writer.write("  " + protobufType.getSimpleName() + " " + field.getName() + " = " + (tagNumber++) + ";");
                writer.newLine();

            } else if (FileScanner.checkNonPrimitive(fieldType)) {

                writer.write("  " + fieldType.getSimpleName() + " " + field.getName() + " = " + (tagNumber++) + ";");
                writer.newLine();
            }
        }
        writer.write("}");
        writer.newLine();
        writer.close();
    }


    private void writeProtobufSchema(Class<?> clazz, String outputDirectoryPath) throws IOException {
        // adding to created files
        schemaGen.add(clazz);

        File file = FileCreator.createFile(clazz, outputDirectoryPath);

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        Set<Class<?>> interfaces, superClass;
        interfaces = FileScanner.analyzeImports(clazz.getInterfaces());
        superClass = FileScanner.analyzeImports(new Class[]{clazz.getSuperclass()});

        // All Imports
        writeHeaders(writer, outputDirectoryPath, clazz, interfaces, superClass);

        // message body
        writeMessage(writer, clazz, interfaces, superClass);
    }
}


