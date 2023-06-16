package org.example;

import animals.Animal;
import animals.Lion;
import org.example.resolver.ProtobufSchemaGenerator;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        ProtobufSchemaGenerator generator = new ProtobufSchemaGenerator();
        generator.generateProtobufSchema(Person.class, "protofiles");

//        Lion lion = Lion.newBuilder()
//                .setId(32)
//                .setCapacity(1220)
//                .setType("Carnivorous")
//                .setAnimalObject(Animal.newBuilder()
//                        .setHunt(true).setName("Animal").build()).build();
//
//
//        // serialising data
//        byte[] serializedData = lion.toByteArray();
//        System.out.println(serializedData);
//        System.out.println();
//
//        // de-serialising data
//        Lion lion1 = Lion.parseFrom(serializedData);
//        System.out.println(lion1);
    }
}

