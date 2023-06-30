package org.example.resolver.fileutilities;

import java.io.*;
import java.util.Set;

public class FileCreator {
    private static final String PROTOVERSION = "syntax = \"proto3\";";
    private static final String JAVA_PKG = "option java_package = ";
    private static final String MULTIPLE_FILES_OPN = "option java_multiple_files = true;";
    private static final String PROTOEXT = ".proto";
    private static final String FILE_CREATE_ERR = "Unable to create file at specified path.";
    private static final String PACKAGE_CREATE_ERR = "Unable to create package at specified path.";
    private static final String DIR_CREATE_ERR = "Unable to create directory at specified path.";

    private FileCreator(){}

    public static void clearFile(String fileName) throws IOException {
        FileWriter fwOb = new FileWriter(fileName, false);
        PrintWriter pwOb = new PrintWriter(fwOb, false);
        pwOb.flush();
        pwOb.close();
        fwOb.close();
    }

    public static File createFile(Class<?> clazz, String outputDirectoryPath) throws IOException {
        String packageName = clazz.getPackageName();
        String packagePath = outputDirectoryPath + File.separator + packageName.replace(".", "/");
        File packageDir = new File(packagePath);
        if (!packageDir.exists()) {
            boolean dirCreated = packageDir.mkdirs();
            if (!dirCreated) {
                throw new IOException(PACKAGE_CREATE_ERR);
            }
        }

        String fileName = packagePath + File.separator + clazz.getSimpleName() + PROTOEXT;
        File file = new File(fileName);
        if (!file.exists()) {
            boolean fileCreated = file.createNewFile();
            if (!fileCreated) {
                throw new IOException(FILE_CREATE_ERR);
            }
        }
        clearFile(fileName);
        return file;
    }

    public static void makeDir(String outputDirectoryPath) throws IOException {

        File outputDirectory = new File(outputDirectoryPath);
        if (!outputDirectory.exists()) {
            boolean dirCreated = outputDirectory.mkdirs();
            if (!dirCreated){
                throw new IOException(DIR_CREATE_ERR);
            }
        }
    }

    public static void fileHeaders(BufferedWriter writer, String packageName) throws IOException {
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

    public static int schemaDependency(BufferedWriter writer, Class<?> clazz, Set<Class<?>> interfaces, Set<Class<?>> superClass) throws IOException {

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

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
