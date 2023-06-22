package org.example;

import org.example.resolver.ProtoSchemaGenerator;
import org.example.test.Person;
import org.example.test.people.Female;
import org.example.test.people.Male;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ProtoSchemaGenerator generator = new ProtoSchemaGenerator();
        generator.generateProtobufSchema(Female.class, "protofiles");
    }
}
