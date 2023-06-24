package org.example.resolver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class ProtoGenerator {
    private static final String JAVA_EXT = ".java";

    public void generateAll(String inputDir , String outputDir) throws IOException, ClassNotFoundException {
        ProtoSchemaGenerator generator = new ProtoSchemaGenerator();
        File currDir = new File(inputDir);
        File[] files = currDir.listFiles();

        assert files != null;
        iterateAllFiles(files, generator, outputDir);
    }

    private static void iterateAllFiles(File[] files, ProtoSchemaGenerator generator, String outputDir) throws IOException, ClassNotFoundException {
        for (File file:files){
            if (file.isFile() && file.getName().endsWith(JAVA_EXT)){

                String fileName = file.getName();
                String packageName = extractPackageName(file.getPath());
                String className = packageName + "." + fileName.replace(JAVA_EXT, "");

                Class<?> clazz = Class.forName(className);
                generateAllSchema(generator, clazz, outputDir);
            }
            else if (file.isDirectory()){
                iterateAllFiles(Objects.requireNonNull(file.listFiles()), generator, outputDir);
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

    private static void generateAllSchema(ProtoSchemaGenerator generator, Class<?> clazz, String outputDirPath) throws IOException {
        generator.generateProtobufSchema(clazz, outputDirPath);
    }
}
