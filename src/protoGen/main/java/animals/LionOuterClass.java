// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: lion.proto

package animals;

public final class LionOuterClass {
  private LionOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_animals_Lion_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_animals_Lion_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_animals_Lion_MapEntry_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_animals_Lion_MapEntry_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_animals_Lion_PairsEntry_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_animals_Lion_PairsEntry_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_animals_MaleLion_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_animals_MaleLion_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_animals_FemaleLion_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_animals_FemaleLion_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\nlion.proto\022\007animals\032\014animal.proto\"\303\001\n\004" +
      "Lion\022#\n\003map\030\001 \003(\0132\026.animals.Lion.MapEntr" +
      "y\022\'\n\005pairs\030\002 \003(\0132\030.animals.Lion.PairsEnt" +
      "ry\032*\n\010MapEntry\022\013\n\003key\030\001 \001(\t\022\r\n\005value\030\002 \001" +
      "(\005:\0028\001\032A\n\nPairsEntry\022\013\n\003key\030\001 \001(\005\022\"\n\005val" +
      "ue\030\002 \001(\0132\023.animals.FemaleLion:\0028\001\"\026\n\010Mal" +
      "eLion\022\n\n\002id\030\001 \001(\005\"\030\n\nFemaleLion\022\n\n\002id\030\001 " +
      "\001(\005B\002P\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          animals.AnimalOuterClass.getDescriptor(),
        });
    internal_static_animals_Lion_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_animals_Lion_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_animals_Lion_descriptor,
        new java.lang.String[] { "Map", "Pairs", });
    internal_static_animals_Lion_MapEntry_descriptor =
      internal_static_animals_Lion_descriptor.getNestedTypes().get(0);
    internal_static_animals_Lion_MapEntry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_animals_Lion_MapEntry_descriptor,
        new java.lang.String[] { "Key", "Value", });
    internal_static_animals_Lion_PairsEntry_descriptor =
      internal_static_animals_Lion_descriptor.getNestedTypes().get(1);
    internal_static_animals_Lion_PairsEntry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_animals_Lion_PairsEntry_descriptor,
        new java.lang.String[] { "Key", "Value", });
    internal_static_animals_MaleLion_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_animals_MaleLion_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_animals_MaleLion_descriptor,
        new java.lang.String[] { "Id", });
    internal_static_animals_FemaleLion_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_animals_FemaleLion_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_animals_FemaleLion_descriptor,
        new java.lang.String[] { "Id", });
    animals.AnimalOuterClass.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
