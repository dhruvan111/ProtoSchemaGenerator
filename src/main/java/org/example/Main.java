package org.example;

import org.example.resolver.ProtoSchemaGenerator;
import test.Person;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ProtoSchemaGenerator generator = new ProtoSchemaGenerator();
        generator.generateProtobufSchema(Person.class, "protofiles");
    }
}
