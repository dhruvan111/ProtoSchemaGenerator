package org.example;

import org.example.resolver.ProtoGenerator;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String path = "/Users/dhruvankadavala/Documents/Protobuf2/src/main/java/testbeans";

        ProtoGenerator generator = new ProtoGenerator();
        generator.generateAllFiles(path, "ProtoFiles", 4);
    }
}
