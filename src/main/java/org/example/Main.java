package org.example;

import org.example.resolver.ProtobufSchemaGenerator;
import test.Person;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ProtobufSchemaGenerator generator = new ProtobufSchemaGenerator();
        generator.generateProtobufSchema(Person.class, "protofiles");
    }
}

