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
    internal_static_animals_Lion_IntegerList_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_animals_Lion_IntegerList_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_animals_Lion_PairsMEntry_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_animals_Lion_PairsMEntry_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_animals_Lion_AddressList_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_animals_Lion_AddressList_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_animals_Address_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_animals_Address_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\nlion.proto\022\007animals\032\014animal.proto\"\206\003\n\004" +
      "Lion\022#\n\003map\030\001 \003(\0132\026.animals.Lion.MapEntr" +
      "y\022\'\n\005pairs\030\002 \003(\0132\030.animals.Lion.PairsEnt" +
      "ry\022)\n\006pairsM\030\003 \003(\0132\031.animals.Lion.PairsM" +
      "Entry\032*\n\010MapEntry\022\013\n\003key\030\001 \001(\t\022\r\n\005value\030" +
      "\002 \001(\005:\0028\001\032G\n\nPairsEntry\022\013\n\003key\030\001 \001(\005\022(\n\005" +
      "value\030\002 \001(\0132\031.animals.Lion.IntegerList:\002" +
      "8\001\032\031\n\013IntegerList\022\n\n\002id\030\001 \003(\005\032H\n\013PairsME" +
      "ntry\022\013\n\003key\030\001 \001(\005\022(\n\005value\030\002 \001(\0132\031.anima" +
      "ls.Lion.AddressList:\0028\001\032+\n\013AddressList\022\034" +
      "\n\002id\030\001 \003(\0132\020.animals.Address\"\t\n\007AddressB" +
      "\002P\001b\006proto3"
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
        new java.lang.String[] { "Map", "Pairs", "PairsM", });
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
    internal_static_animals_Lion_IntegerList_descriptor =
      internal_static_animals_Lion_descriptor.getNestedTypes().get(2);
    internal_static_animals_Lion_IntegerList_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_animals_Lion_IntegerList_descriptor,
        new java.lang.String[] { "Id", });
    internal_static_animals_Lion_PairsMEntry_descriptor =
      internal_static_animals_Lion_descriptor.getNestedTypes().get(3);
    internal_static_animals_Lion_PairsMEntry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_animals_Lion_PairsMEntry_descriptor,
        new java.lang.String[] { "Key", "Value", });
    internal_static_animals_Lion_AddressList_descriptor =
      internal_static_animals_Lion_descriptor.getNestedTypes().get(4);
    internal_static_animals_Lion_AddressList_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_animals_Lion_AddressList_descriptor,
        new java.lang.String[] { "Id", });
    internal_static_animals_Address_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_animals_Address_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_animals_Address_descriptor,
        new java.lang.String[] { });
    animals.AnimalOuterClass.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
