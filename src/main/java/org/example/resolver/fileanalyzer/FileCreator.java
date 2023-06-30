package org.example.resolver.fileanalyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileCreator {
    private static final String FILE_CREATE_ERR = "Unable to create file at specified path.";
    private static final String PACKAGE_CREATE_ERR = "Unable to create package at specified path.";
    private static final String PROTOEXT = ".proto";

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
}
