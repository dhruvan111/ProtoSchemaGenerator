package org.example.resolver;

import org.example.resolver.prototypes.*;

import java.util.*;

public class ProtobufUtils {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TYPE_MAPPING = new HashMap<>();
    private static final Set<Class<?>> PRIMITIVE_MAP_MAPPING = new HashSet<>();
    private static final Map<Class<?>, Class<?>> PRIMITIVE_KEY_MAPPING = new HashMap<>();
    private static final Set<Class<?>> PRIMITIVE_LIST_MAPPING = new HashSet<>();

    static {
        PRIMITIVE_TYPE_MAPPING.put(boolean.class, bool.class);
        PRIMITIVE_TYPE_MAPPING.put(byte.class, int32.class);
        PRIMITIVE_TYPE_MAPPING.put(short.class, int32.class);
        PRIMITIVE_TYPE_MAPPING.put(int.class, int32.class);
        PRIMITIVE_TYPE_MAPPING.put(long.class, int64.class);
        PRIMITIVE_TYPE_MAPPING.put(float.class, float.class);
        PRIMITIVE_TYPE_MAPPING.put(double.class, double.class);
        PRIMITIVE_TYPE_MAPPING.put(java.lang.String.class, string.class);
        PRIMITIVE_TYPE_MAPPING.put(java.lang.Integer.class, int32.class);
        PRIMITIVE_TYPE_MAPPING.put(java.lang.Long.class, int64.class);
    }

    static {
        // for Collection types
        PRIMITIVE_LIST_MAPPING.add(List.class);
        PRIMITIVE_LIST_MAPPING.add(ArrayList.class);
        PRIMITIVE_LIST_MAPPING.add(LinkedList.class);
        PRIMITIVE_LIST_MAPPING.add(Vector.class);
        PRIMITIVE_LIST_MAPPING.add(Stack.class);

        PRIMITIVE_LIST_MAPPING.add(Queue.class);
        PRIMITIVE_LIST_MAPPING.add(Deque.class);
        PRIMITIVE_LIST_MAPPING.add(PriorityQueue.class);
        PRIMITIVE_LIST_MAPPING.add(ArrayDeque.class);

        PRIMITIVE_LIST_MAPPING.add(Set.class);
        PRIMITIVE_LIST_MAPPING.add(HashSet.class);
        PRIMITIVE_LIST_MAPPING.add(LinkedHashSet.class);
        PRIMITIVE_LIST_MAPPING.add(SortedSet.class);
        PRIMITIVE_LIST_MAPPING.add(TreeSet.class);
    }

    static {
        //for Map types
        PRIMITIVE_MAP_MAPPING.add(Map.class);
        PRIMITIVE_MAP_MAPPING.add(SortedMap.class);
        PRIMITIVE_MAP_MAPPING.add(TreeMap.class);
        PRIMITIVE_MAP_MAPPING.add(HashMap.class);
        PRIMITIVE_MAP_MAPPING.add(LinkedHashMap.class);

    }

    static {
        // primitive key types
        PRIMITIVE_KEY_MAPPING.put(boolean.class, bool.class);
        PRIMITIVE_KEY_MAPPING.put(byte.class, int32.class);
        PRIMITIVE_KEY_MAPPING.put(short.class, int32.class);
        PRIMITIVE_KEY_MAPPING.put(int.class, int32.class);
        PRIMITIVE_KEY_MAPPING.put(long.class, int64.class);
        PRIMITIVE_KEY_MAPPING.put(java.lang.String.class, string.class);
        PRIMITIVE_KEY_MAPPING.put(java.lang.Integer.class, int32.class);
        PRIMITIVE_KEY_MAPPING.put(java.lang.Long.class, int64.class);
    }

    private ProtobufUtils(){}

    public static boolean isPrimitiveType(Class<?> clazz) {
        return PRIMITIVE_TYPE_MAPPING.containsKey(clazz);
    }

    public static Class<?> getProtobufType(Class<?> clazz) {
        return PRIMITIVE_TYPE_MAPPING.get(clazz);
    }

    public static boolean isPrimitiveListType(Class<?> clazz){
        return PRIMITIVE_LIST_MAPPING.contains(clazz);
    }

    public static Class<?> getProtoListType(){
        return (repeated.class);
    }

    public static boolean isPrimitiveMapType(Class<?> clazz){
        return PRIMITIVE_MAP_MAPPING.contains(clazz);
    }

    public static Class<?> getProtoMapType(){
        return (map.class);
    }

    public static boolean isPrimitiveKeyType(Class<?> clazz){
        return PRIMITIVE_KEY_MAPPING.containsKey(clazz);
    }

    public static Class<?> getProtoKeyType(Class<?> clazz){
        return PRIMITIVE_KEY_MAPPING.get(clazz);
    }
}
