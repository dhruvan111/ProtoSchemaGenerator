package org.example.resolver;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.Buffer;
import java.util.*;

public class ProtobufSchemaGenerator {

    private Set<Class<?>> visitedClasses;
    private Set<Class<?>> dependencies;

    public void generateProtobufSchema(Class<?> rootClass, String outputDirectoryPath) throws IOException {

        makeDirAndTraverse(rootClass, outputDirectoryPath);
        // creating .proto file for rootClass
        writeProtobufSchema(rootClass, outputDirectoryPath);
    }

    private void makeDirAndTraverse(Class<?> rootClass, String outputDirectoryPath) throws IOException {
        visitedClasses = new HashSet<>();
        dependencies = new HashSet<>();

        traverseClass(rootClass);
        for (Class<?> dependency : dependencies) {
            traverseClass(dependency);
        }

        File outputDirectory = new File(outputDirectoryPath);
        if (!outputDirectory.exists()) {
            boolean dirCreated = outputDirectory.mkdirs();
            if (!dirCreated){
                throw new IOException("Unable to create file at specified path. It already exists");
            }
        }
    }

    private void traverseClass(Class<?> clazz) {
        if (!visitedClasses.contains(clazz)) {
            visitedClasses.add(clazz);
            analyzeFields(clazz.getDeclaredFields());
            analyzeImports(clazz.getInterfaces());
            analyzeImports(new Class[]{clazz.getSuperclass()});
        }
    }

    private void analyzeFields(Field[] fields) {
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            if (checkListType(fieldType)){
                // now taking out its generic class
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    Type[] typeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
                    if (typeArguments.length > 0 && typeArguments[0] instanceof Class<?> genericClass) {
                        traverseClass(genericClass);
                        dependencies.add(genericClass);
                    }
                }
            }
            else if (!fieldType.isPrimitive() && !fieldType.getPackage().getName().startsWith("java.")) {
                traverseClass(fieldType);
                dependencies.add(fieldType);
            }
        }
    }

    private void analyzeImports(Class<?>[] interfaces) {
        for (Class<?> iface : interfaces) {
            if (iface == null){
                continue;
            }
            if (!iface.isPrimitive() && !iface.getPackage().getName().startsWith("java.")) {
                traverseClass(iface);
                dependencies.add(iface);
            }
        }
    }

    public static void clearFile(String fileName) throws IOException {
        FileWriter fwOb = new FileWriter(fileName, false);
        PrintWriter pwOb = new PrintWriter(fwOb, false);
        pwOb.flush();
        pwOb.close();
        fwOb.close();
    }

    private boolean checkListType(Class<?> fieldType){
        return fieldType.equals(List.class) || fieldType.equals(ArrayList.class) || fieldType.equals(LinkedList.class);
    }

    private void checkAndUpdateList(Field field, Set<Class<?>> importDone, BufferedWriter writer, String outputDirectoryPath) throws IOException {
        Class<?> fieldType = field.getType();
        if (checkListType(fieldType)){
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                Type[] typeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
                if (typeArguments.length > 0 && typeArguments[0] instanceof Class<?> genericClass) {

                    if (!importDone.contains(genericClass)){
                        if (visitedClasses.contains(genericClass)){
                            importWithCall(genericClass, importDone, writer, outputDirectoryPath);
                        }else{
                            onlyImport(genericClass, importDone, writer);
                        }
                    }
                }
            }
        }
    }

    private void onlyImport(Class<?> fieldType, Set<Class<?>> importDone, BufferedWriter writer) throws IOException {
        importDone.add(fieldType);
        writer.write("import \"" + fieldType.getSimpleName() + ".proto\";");
        writer.newLine();
    }

    private void importWithCall(Class<?> fieldType, Set<Class<?>> importDone, BufferedWriter writer, String outputDirectoryPath) throws IOException {
        importDone.add(fieldType);
        writer.write("import \"" + fieldType.getSimpleName() + ".proto\";");
        writer.newLine();
        // recursively making .proto files for all non-primitive files
        writeProtobufSchema(fieldType, outputDirectoryPath);
    }

    private void writeHeaders(BufferedWriter writer, String outputDirectoryPath, Class<?> clazz, Field[] fields) throws IOException {
        writer.write("syntax = \"proto3\";");
        writer.newLine();
        writer.newLine();

        writer.write("package personData;");
        writer.newLine();
        writer.newLine();

        Set<Class<?>> importDone = new HashSet<>();

        for (Field field : fields) {

            Class<?> fieldType = field.getType();
            if (importDone.contains(fieldType)){
                continue;
            }
            else if (ProtobufUtils.isPrimitiveType(fieldType)){
                // check for List types and update if generic Type present
                checkAndUpdateList(field, importDone, writer, outputDirectoryPath);
            }
            else if (visitedClasses.contains(fieldType)) {
                // import with recursive call
                importWithCall(fieldType, importDone, writer, outputDirectoryPath);
            }
            else {
                // only import in case of cyclic dependency or dependency file has been made
                onlyImport(fieldType, importDone, writer);
            }
        }
    }


    private void writeMessage(BufferedWriter writer, Class<?> clazz, Field[] fields) throws IOException {
        writer.newLine();
        writer.write("message " + clazz.getSimpleName() + " {");
        writer.newLine();

        int tagNumber = 1;
        for (Field field : fields) {
            Class<?> fieldType = field.getType();

            if (checkListType(fieldType)){
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    Type[] typeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
                    if (typeArguments.length > 0 && typeArguments[0] instanceof Class<?> genericClass) {
                        writer.write("  repeated " + genericClass.getSimpleName() + " " + field.getName() + " = " + (tagNumber++) + ";");
                        writer.newLine();
                    }
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
        // removing classes to prevent from multiple recursive calls
        visitedClasses.remove(clazz);
        String fileName = outputDirectoryPath + "/" + clazz.getSimpleName() + ".proto";
        File file = new File(fileName);
        if (!file.exists()){
            boolean fileCreated =  file.createNewFile();
            if (!fileCreated){
              throw new IOException("Unable to create file at specified path. It already exists");
            }
        }
        clearFile(fileName);

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        Field[] fields = clazz.getDeclaredFields();
        // Header writing
        writeHeaders(writer, outputDirectoryPath, clazz, fields);

        // message writing
        writeMessage(writer, clazz, fields);
    }
}
