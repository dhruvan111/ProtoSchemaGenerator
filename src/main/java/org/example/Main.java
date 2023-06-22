package org.example;

import org.example.resolver.ProtoSchemaGenerator;
import test.UpdateField;

import java.io.IOException;
import java.lang.reflect.Field;

public class Main {
    public static void main(String[] args) throws IOException {
        ProtoSchemaGenerator generator = new ProtoSchemaGenerator();
        generator.generateProtobufSchema(UpdateField.class, "protofiles");
        
    }
}
