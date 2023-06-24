package org.example;

import org.example.resolver.ProtoGenerator;

import java.io.IOException;

public class Main {
    private static final String inputDir = "/Users/dhruvankadavala/Documents/Protobuf2/src/main/java/test";
    private static final String outputDir = "ProtoFiles";
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ProtoGenerator generator = new ProtoGenerator();
        generator.generateAll(inputDir, outputDir);
    }
}
