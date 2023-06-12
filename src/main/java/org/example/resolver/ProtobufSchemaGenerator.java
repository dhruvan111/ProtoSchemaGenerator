package org.example.resolver;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class ProtobufSchemaGenerator {

    private Set<Class<?>> visitedClasses;
    private Set<Class<?>> dependencies;

    public void generateProtobufSchema(Class<?> rootClass, String outputDirectoryPath) throws IOException {
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

        writeProtobufSchema(rootClass, outputDirectoryPath);
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
            if (!fieldType.isPrimitive() && !fieldType.getPackage().getName().startsWith("java.")) {
                traverseClass(fieldType);
                dependencies.add(fieldType);
            }
        }
    }

    private void analyzeImports(Class<?>[] interfaces) {
        for (Class<?> iface : interfaces) {
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

    private void writeProtobufSchema(Class<?> clazz, String outputDirectoryPath) throws IOException {
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

        writer.write("syntax = \"proto3\";");
        writer.newLine();
        writer.newLine();

        writer.write("package personData;");
        writer.newLine();
        writer.newLine();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            if (!fieldType.isPrimitive() && !fieldType.getPackage().getName().startsWith("java.")) {
                writer.write("import \"" + fieldType.getSimpleName() + ".proto\";");
                writer.newLine();
                // recursively making .proto files for all non-primitive files
                generateProtobufSchema(fieldType, outputDirectoryPath);
            }
        }

        writer.newLine();

        writer.write("message " + clazz.getSimpleName() + " {");
        writer.newLine();


        int tagNumber = 1;
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            if (ProtobufUtils.isPrimitiveType(fieldType)) {
                Class<?> protobufType = ProtobufUtils.getProtobufType(fieldType);
                writer.write("  " + protobufType.getSimpleName() + " " + field.getName() + " = " + (tagNumber++) + ";");
                writer.newLine();
            } else if (!fieldType.isPrimitive() && !fieldType.getPackage().getName().startsWith("java.")) {

                writer.write("  " + fieldType.getSimpleName() + " " + field.getName() + " = " + (tagNumber++) + ";");
                writer.newLine();
            }
        }
        // adding comment for v1.2
        writer.write("}");
        writer.newLine();
        writer.close();
    }
}
