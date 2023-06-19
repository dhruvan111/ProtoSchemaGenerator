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
                dependencies.addAll(analyzeNestedDependency(genericType));
            }

            // checking for nestedMap Type
            else if (ProtobufUtils.isPrimitiveMapType(fieldType)){
                Type genericType = field.getGenericType();
                dependencies.addAll(analyzeNestedDependency(genericType));
            }

            else if (!fieldType.isPrimitive() && !fieldType.getPackage().getName().startsWith("java.")) {
                dependencies.add(fieldType);
            }
        }
        return dependencies;
    }

    private Set<Class<?>> analyzeNestedDependency(Type type){
        Set<Class<?>> currSet = new HashSet<>();
        if (type instanceof ParameterizedType){
            Type[] typeArgument = ((ParameterizedType) type).getActualTypeArguments();
            if (typeArgument.length == 1){
                // List Type
                currSet.addAll(analyzeNestedDependency(typeArgument[0]));
            }
            else {
                // Map Type
                currSet.addAll(analyzeNestedDependency(typeArgument[0]));
                currSet.addAll(analyzeNestedDependency(typeArgument[1]));
            }
        }
        else if (type instanceof Class<?> innerClass) {
            currSet.add(innerClass);
        }
        return currSet;
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

    private void createList(BufferedWriter writer, Type[] typeArguments, Field field, int tagNumber, int cnt) throws IOException {

        String nestedListName = capitalize(field.getName()) + "List";
        if (typeArguments.length>0 && typeArguments[0] instanceof ParameterizedType){

            Type[] typeArguments2 = ((ParameterizedType) typeArguments[0]).getActualTypeArguments();
            String currListName = nestedListName;
            String elementName = field.getName();
            if (cnt!=0){
                currListName += cnt;
                elementName += cnt;
            }

            writer.write("  ".repeat(Math.max(0, cnt)));
            writer.write("  repeated " + currListName + " " + elementName + " = " + (tagNumber) + ";");
            writer.newLine();

            writer.write("  ".repeat(Math.max(0, cnt)));
            writer.write("  message " + currListName + " {");
            writer.newLine();
            writer.newLine();

            cnt++;
            if (typeArguments2.length == 1){
                // Nested List
                createList(writer, typeArguments2, field, 1, cnt);
            }
            else {
                // Nested Map
                if (checkSimpleMap(typeArguments2)){
                    simpleMap(typeArguments2, field, writer, tagNumber, cnt-1);
                }
                else {
                    complexMap(typeArguments2, field, writer, 1, cnt-1);
                }
            }
            writer.write("  }");
        }
        else if (typeArguments.length>0 && typeArguments[0] instanceof Class<?> innerClass){

            String className = innerClass.getSimpleName();
            if (ProtobufUtils.isPrimitiveType(innerClass)){
                className = ProtobufUtils.getProtobufType(innerClass).getSimpleName();
            }
            String elementName = field.getName() + cnt;
            writer.write("  ".repeat(Math.max(0, cnt)));
            writer.write("  repeated " + className + " " + elementName + " = " + (tagNumber) + ";");
            writer.newLine();
        }
    }

    private void complexMap(Type[] typeArguments, Field field, BufferedWriter writer, int tagNumber, int cnt) throws IOException {
        Type firstArg = typeArguments[0];
        Type secondArg = typeArguments[1];
        String schemaName = field.getName() + "Entry";
        String mapName = field.getName();
        if (cnt != 0){
            mapName = "pair";
            schemaName += cnt;
        }

        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write("  repeated " + schemaName + " " + mapName + " = " + tagNumber + ";");
        writer.newLine();

        writer.write("  ".repeat(Math.max(0, cnt)));
        writer.write("  message " + schemaName + " {");
        writer.newLine();
        writer.newLine();

        if (firstArg instanceof ParameterizedType){
            Type[] typeArguments2 = ((ParameterizedType) firstArg).getActualTypeArguments();
            if (typeArguments2.length == 1){
                // List Type
                createList(writer, typeArguments2, field, 1, cnt);
            }
            else {
                // Map Type
                if (checkSimpleMap(typeArguments2)){
                    simpleMap(typeArguments2, field, writer, 1, cnt);
                }
                else {
                    complexMap(typeArguments2, field, writer, 1, cnt);
                }
            }
        }

        else if (firstArg instanceof Class<?> innerClass){
            String keyName = innerClass.getSimpleName();
            if (ProtobufUtils.isPrimitiveMapType(innerClass)){
                keyName = ProtobufUtils.getProtoMapType(innerClass).getSimpleName();
            }
            writer.write("  ".repeat(Math.max(0, cnt)));
            writer.write("  " + keyName + " key = 1;");
            writer.newLine();
        }

        // checking for second Arg
        if (secondArg instanceof ParameterizedType){
            Type[] typeArguments2 = ((ParameterizedType) secondArg).getActualTypeArguments();
            if (typeArguments2.length == 1){
                // List Type
                createList(writer, typeArguments2, field, 2, cnt);
            }
            else {
                // Map Type
                if (checkSimpleMap(typeArguments2)){
                    simpleMap(typeArguments2, field, writer, 2, cnt);
                }
                else {
                    complexMap(typeArguments2, field, writer, 2, cnt);
                }
            }
        }
        else if (secondArg instanceof Class<?> innerClass){
            String keyName = innerClass.getSimpleName();
            if (ProtobufUtils.isPrimitiveMapType(innerClass)){
                keyName = ProtobufUtils.getProtoMapType(innerClass).getSimpleName();
            }
            writer.write("  ".repeat(Math.max(0, cnt)));
            writer.write("  " + keyName + " value = 2;");
            writer.newLine();
        }
        writer.write("  }");
        writer.newLine();

    }

    private boolean checkSimpleMap(Type[] typeArguments){
        if (typeArguments[0] instanceof Class<?> nestedClass){
            return ProtobufUtils.isPrimitiveMapType(nestedClass);
        }
        return false;
    }

    private void simpleMap(Type[] typeArguments, Field field, BufferedWriter writer, int tagNumber, int cnt) throws IOException {
        Type firstArg = typeArguments[0];
        Type secondArg = typeArguments[1];

        Class<?> firstArgClass = (Class<?>) firstArg;

        // here firstArg is primitive for Map
        if (secondArg instanceof ParameterizedType){
            Type[] typeArguments2 = ((ParameterizedType) secondArg).getActualTypeArguments();
            String secondClass = field.getName() + "Entry";
            String mapName = "map" + cnt;

            writer.write("  ".repeat(Math.max(0, cnt)));
            writer.write("  map<" + firstArgClass.getSimpleName() + "," + secondClass + "> " + mapName + ";");
            writer.newLine();

            writer.write("  ".repeat(Math.max(0, cnt)));
            writer.write("  message " + secondClass + "{");
            writer.newLine();
            cnt++;
            if (typeArguments2.length == 1){
                // List Type
                createList(writer, typeArguments2, field, tagNumber, cnt);
            }
            else {
                // Map Type
                if (checkSimpleMap(typeArguments2)) {
                    simpleMap(typeArguments2, field, writer, tagNumber, cnt);
                }
                else{
                    complexMap(typeArguments2, field, writer, tagNumber, cnt);
                }
            }
        }
        else if (secondArg instanceof Class<?> secondArgClass){

            String mapName = "map" + cnt;
            writer.write("  ".repeat(Math.max(0, cnt)));
            writer.write("  map<" + firstArgClass.getSimpleName() + "," + secondArgClass + "> " + mapName + ";");
            writer.newLine();

            for (int i=cnt; i>0; i--){
                writer.write("  ".repeat(i-1));
                writer.write("  }");
                writer.newLine();
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

    private int listScan(Field field, int tagNumber, BufferedWriter writer) throws IOException {
        Type genericType = field.getGenericType();

        if (genericType instanceof ParameterizedType) {

            Type[] typeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
            createList(writer, typeArguments,field, tagNumber, 0);
            tagNumber++;
            writer.newLine();
        }
        return tagNumber;
    }

    private int mapScan(Field field, int tagNumber, BufferedWriter writer) throws IOException {
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

    private void writeMessage(BufferedWriter writer, Class<?> clazz, Set<Class<?>> interfaces, Set<Class<?>> superClass) throws IOException {

        int tagNumber = schemaDependency(writer, clazz, interfaces, superClass);

        // for every Field
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();

            // checking for any Collection Type
            if (ProtobufUtils.isPrimitiveListType(fieldType)){
                tagNumber = listScan(field, tagNumber, writer);
            }

            // Checking for Map Type
            else if (ProtobufUtils.isPrimitiveMapType(fieldType)){
                tagNumber = mapScan(field, tagNumber, writer);
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
