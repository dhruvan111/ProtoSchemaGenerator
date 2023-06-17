package org.example.resolver;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class ProtobufSchemaGenerator {

    private Set<Class<?>> schemaGen;
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
                throw new IOException("Unable to create file at specified path. It already exists");
            }
        }
    }

    private Set<Class<?>> analyzeFields(Field[] fields) {
        Set<Class<?>> dependencies;
        dependencies = new HashSet<>();

        for (Field field : fields) {
            Class<?> fieldType = field.getType();

            // checking for nestedList Type
            if (ProtobufUtils.isPrimitiveListType(fieldType)) {
                Type genericType = field.getGenericType();

                if (genericType instanceof ParameterizedType) {

                    Type[] typeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
                    while (typeArguments.length>0 && typeArguments[0] instanceof ParameterizedType){
                        typeArguments = ((ParameterizedType) typeArguments[0]).getActualTypeArguments();
                    }
                    if (typeArguments.length > 0 && typeArguments[0] instanceof Class<?> innerClass){
                        dependencies.add(innerClass);
                    }
                }
            }

            else if (!fieldType.isPrimitive() && !fieldType.getPackage().getName().startsWith("java.")) {
                dependencies.add(fieldType);
            }
        }
        return dependencies;
    }

    private Set<Class<?>> analyzeImports(Class<?>[] interfaces) {
        Set<Class<?>> dependencies;
        dependencies = new HashSet<>();
        for (Class<?> iface : interfaces) {
            if (iface == null){
                continue;
            }
            if (!iface.isPrimitive() && !iface.getPackage().getName().startsWith("java.")) {
                dependencies.add(iface);
            }
        }
        return dependencies;
    }

    public static void clearFile(String fileName) throws IOException {
        FileWriter fwOb = new FileWriter(fileName, false);
        PrintWriter pwOb = new PrintWriter(fwOb, false);
        pwOb.flush();
        pwOb.close();
        fwOb.close();
    }

    private void importWithoutCall(Class<?> fieldType, BufferedWriter writer) throws IOException {
        writer.write("import \"" + fieldType.getSimpleName() + ".proto\";");
        writer.newLine();
    }

    private void importWithCall(Class<?> fieldType,BufferedWriter writer, String outputDirectoryPath) throws IOException {
        writer.write("import \"" + fieldType.getSimpleName() + ".proto\";");
        writer.newLine();
        // recursively making .proto files for all non-primitive files
        writeProtobufSchema(fieldType, outputDirectoryPath);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }


    private void hardCodeHeaders(BufferedWriter writer) throws IOException {
        writer.write("syntax = \"proto3\";");
        writer.newLine();
        writer.newLine();

        writer.write("package personData;");
        writer.newLine();
        writer.newLine();

        writer.write("option java_multiple_files = true;");
        writer.newLine();
        writer.newLine();
    }


    private void writeHeaders(BufferedWriter writer, String outputDirectoryPath, Class<?> clazz, Set<Class<?>> interfaces, Set<Class<?>> superClass) throws IOException {

        Set<Class<?>> importDone, fields;
        importDone = new HashSet<>();
        fields = analyzeFields(clazz.getDeclaredFields());

        hardCodeHeaders(writer);

        // Import for interfaces & Parent class
        for (Class<?> dependency:interfaces){
            if (!schemaGen.contains(dependency)){
                importWithCall(dependency, writer, outputDirectoryPath);
                importDone.add(dependency);
            }
            else if (!importDone.contains(dependency)){
                importWithoutCall(dependency, writer);
                importDone.add(dependency);
            }
        }
        for (Class<?> dependency:superClass){
            if (!schemaGen.contains(dependency)){
                importWithCall(dependency, writer, outputDirectoryPath);
                importDone.add(dependency);
            }
            else if (!importDone.contains(dependency)){
                importWithoutCall(dependency, writer);
                importDone.add(dependency);
            }
        }

        // Imports for Fields
        for (Class<?> dependency:fields){
            if (ProtobufUtils.isPrimitiveType(dependency)){
                continue;
            }
            if (!schemaGen.contains(dependency)){
                importWithCall(dependency, writer, outputDirectoryPath);
                importDone.add(dependency);
            }
            else if (!importDone.contains(dependency)){
                importWithoutCall(dependency, writer);
                importDone.add(dependency);
            }
        }
    }

    private void nestedList(BufferedWriter writer, Type[] typeArguments, String nestedListName, Field field, int tagNumber, int cnt) throws IOException {

        if (typeArguments.length>0 && typeArguments[0] instanceof ParameterizedType){
            String currListName = null;
            // again nested List
            if (cnt>0){
                currListName = nestedListName + cnt;
            }
            else {
                currListName = nestedListName;
            }
            writer.write("  ".repeat(Math.max(0, cnt)));
            writer.write("  repeated " + currListName + " " + field.getName() + " = " + (tagNumber) + ";");
            writer.newLine();

            writer.write("  ".repeat(Math.max(0, cnt)));
            writer.write("  message " + currListName + " {");
            writer.newLine();
            writer.newLine();
            Type[] typeArguments2 = ((ParameterizedType) typeArguments[0]).getActualTypeArguments();
            cnt++;
            nestedList(writer, typeArguments2, nestedListName, field, tagNumber, cnt);
        }
        else if (typeArguments.length>0 && typeArguments[0] instanceof Class<?> innerClass){

            String elementName = null;
            if (ProtobufUtils.isPrimitiveType(innerClass)){
                elementName = ProtobufUtils.getProtobufType(innerClass).getSimpleName();
            }
            else{
                elementName = innerClass.getSimpleName();
            }

            writer.write("  ".repeat(Math.max(0, cnt)));
            writer.write("  repeated " + elementName + " " + field.getName() + " = " + (tagNumber) + ";");
            writer.newLine();
            for (int i=cnt; i>0; i--){
                writer.write("  ".repeat(i-1));
                writer.write("  }");
                writer.newLine();
            }
        }
    }

    private void writeMessage(BufferedWriter writer, Class<?> clazz, Set<Class<?>> interfaces, Set<Class<?>> superClass) throws IOException {

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

        // for every Field
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();

            // checking for any Collection Type
            if (ProtobufUtils.isPrimitiveListType(fieldType)){
                String nestedListName = capitalize(field.getName()) + "List";
                Type genericType = field.getGenericType();

                if (genericType instanceof ParameterizedType) {

                    Type[] typeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
                    nestedList(writer, typeArguments, nestedListName, field, tagNumber, 0);
                    tagNumber++;
                    writer.newLine();
                }
            }

            else if (ProtobufUtils.isPrimitiveType(fieldType)) {

                Class<?> protobufType = ProtobufUtils.getProtobufType(fieldType);
                writer.write("  " + protobufType.getSimpleName() + " " + field.getName() + " = " + (tagNumber++) + ";");
                writer.newLine();

            } else if (!fieldType.isPrimitive() && !fieldType.getPackage().getName().startsWith("java.")) {

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

        String fileName = outputDirectoryPath + "/" + clazz.getSimpleName().toLowerCase() + ".proto";
        File file = new File(fileName);
        if (!file.exists()){
            boolean fileCreated =  file.createNewFile();
            if (!fileCreated){
              throw new IOException("Unable to create file at specified path. It already exists");
            }
        }
        clearFile(fileName);

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        Set<Class<?>> interfaces, superClass;
        interfaces = analyzeImports(clazz.getInterfaces());
        superClass = analyzeImports(new Class[]{clazz.getSuperclass()});

        // All Imports
        writeHeaders(writer, outputDirectoryPath, clazz, interfaces, superClass);

        // message body
        writeMessage(writer, clazz, interfaces, superClass);
    }
}
