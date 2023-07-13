package org.example;

import org.example.resolver.ProtoGenerator;

public class Main {
    public static void main(String[] args) throws Exception {
        String path = "/Users/dhruvankadavala/Documents/Protobuf2/src/main/java/test";
        ProtoGenerator generator = new ProtoGenerator();
        generator.generateAllFiles(path, "ProtoFiles");
    }
}
