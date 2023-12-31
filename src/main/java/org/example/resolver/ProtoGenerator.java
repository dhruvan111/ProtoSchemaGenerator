package org.example.resolver;

import org.example.resolver.fileutilities.FileAnalyzer;
import org.example.resolver.generator.SchemaGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProtoGenerator {
    private static final String JAVA_EXT = ".java";
    private static final String FILE_NOT_GENERATED = "File not generated";
    public void generateAllFiles(String inputDir , String outputDir) throws IOException, ClassNotFoundException {
        File currDir = new File(inputDir);
        File[] files = currDir.listFiles();

        if (files == null){
            return;
        }
        ExecutorService service = Executors.newFixedThreadPool(1);
        iterateAllFiles(files, outputDir, service);
        service.shutdown();
    }

    public void generateOneFile(Class<?> clazz, String outputDir) throws IOException {
        SchemaGenerator generator = new SchemaGenerator();
        generator.generateProtobufSchema(clazz, outputDir);
    }

    private void iterateAllFiles(File[] files, String outputDir, ExecutorService service) throws IOException, ClassNotFoundException {
        for (File file:files){
            if (file.isFile() && file.getName().endsWith(JAVA_EXT)){

                String fileName = file.getName();
                String packageName = FileAnalyzer.extractPackageName(file.getPath());
                String className = packageName + "." + fileName.replace(JAVA_EXT, "");

                Class<?> clazz = Class.forName(className);
                generateAllSchema(clazz, outputDir, service);
            }
            else if (file.isDirectory()){
                iterateAllFiles(Objects.requireNonNull(file.listFiles()), outputDir, service);
            }
        }
    }


    private void generateAllSchema(Class<?> clazz, String outputDirPath, ExecutorService service) {

        Runnable task = () -> {
            try {
                SchemaGenerator generator = new SchemaGenerator();
                generator.generateProtobufSchema(clazz, outputDirPath);
            } catch (IOException e) {
                throw new FileNotGenerated(FILE_NOT_GENERATED + " for " + clazz);
            }
        };
        service.execute(task);
    }

    private static class FileNotGenerated extends RuntimeException{
        public FileNotGenerated(String message) {
            super(message);
        }
    }
}
