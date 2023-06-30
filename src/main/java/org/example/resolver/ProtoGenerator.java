package org.example.resolver;

import org.example.resolver.generator.SchemaGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class ProtoGenerator {
    private static final String JAVA_EXT = ".java";
    public void generateAllFiles(String inputDir , String outputDir) throws IOException, ClassNotFoundException {
        File currDir = new File(inputDir);
        File[] files = currDir.listFiles();

        assert files != null;
        iterateAllFiles(files, outputDir);
    }

    public void generateOneFile(Class<?> clazz, String outputDir) throws IOException {
        SchemaGenerator generator = new SchemaGenerator();
        generator.generateProtobufSchema(clazz, outputDir);
    }

    private static void iterateAllFiles(File[] files, String outputDir) throws IOException, ClassNotFoundException {
        for (File file:files){
            if (file.isFile() && file.getName().endsWith(JAVA_EXT)){

                String fileName = file.getName();
                String packageName = extractPackageName(file.getPath());
                String className = packageName + "." + fileName.replace(JAVA_EXT, "");

                Class<?> clazz = Class.forName(className);
                generateAllSchema(clazz, outputDir);
            }
            else if (file.isDirectory()){
                iterateAllFiles(Objects.requireNonNull(file.listFiles()), outputDir);
            }
        }
    }

    private static String extractPackageName(String filePath) throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("package ")) {
                    return line.substring("package ".length(), line.indexOf(';')).trim();
                }
            }
        }
        return "";
    }

    private static void generateAllSchema(Class<?> clazz, String outputDirPath) throws IOException {
        SchemaGenerator generator = new SchemaGenerator();
        generator.generateProtobufSchema(clazz, outputDirPath);
    }
}
