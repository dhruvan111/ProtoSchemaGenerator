package org.example.resolver;

import org.example.resolver.prototypes.*;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class ProtobufUtils {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TYPE_MAPPING = new HashMap<>();
    private static final Map<Class<?>, Class<?>> PRIMITIVE_LIST_MAPPING = new HashMap<>();

    static {
        PRIMITIVE_TYPE_MAPPING.put(boolean.class, int32.class);
        PRIMITIVE_TYPE_MAPPING.put(byte.class, int32.class);
        PRIMITIVE_TYPE_MAPPING.put(short.class, int32.class);
        PRIMITIVE_TYPE_MAPPING.put(int.class, int32.class);
        PRIMITIVE_TYPE_MAPPING.put(long.class, int64.class);
        PRIMITIVE_TYPE_MAPPING.put(java.lang.Integer.class, int32.class);
        PRIMITIVE_TYPE_MAPPING.put(float.class, float.class);
        PRIMITIVE_TYPE_MAPPING.put(double.class, double.class);
        PRIMITIVE_TYPE_MAPPING.put(java.lang.String.class, string.class);
    }

    static {
        // for Collection types
        PRIMITIVE_LIST_MAPPING.put(List.class, repeated.class);
        PRIMITIVE_LIST_MAPPING.put(ArrayList.class, repeated.class);
        PRIMITIVE_LIST_MAPPING.put(LinkedList.class, repeated.class);
        PRIMITIVE_LIST_MAPPING.put(Vector.class, repeated.class);
        PRIMITIVE_LIST_MAPPING.put(Stack.class, repeated.class);

        PRIMITIVE_LIST_MAPPING.put(Queue.class, repeated.class);
        PRIMITIVE_LIST_MAPPING.put(Deque.class, repeated.class);
        PRIMITIVE_LIST_MAPPING.put(PriorityQueue.class, repeated.class);
        PRIMITIVE_LIST_MAPPING.put(ArrayDeque.class, repeated.class);

        PRIMITIVE_LIST_MAPPING.put(Set.class, repeated.class);
        PRIMITIVE_LIST_MAPPING.put(HashSet.class, repeated.class);
        PRIMITIVE_LIST_MAPPING.put(LinkedHashSet.class, repeated.class);
        PRIMITIVE_LIST_MAPPING.put(SortedSet.class, repeated.class);
        PRIMITIVE_LIST_MAPPING.put(TreeSet.class, repeated.class);
    }

    public static boolean isPrimitiveType(Class<?> clazz) {
        return PRIMITIVE_TYPE_MAPPING.containsKey(clazz);
    }

    public static Class<?> getProtobufType(Class<?> clazz) {
        return PRIMITIVE_TYPE_MAPPING.get(clazz);
    }

    public static boolean isPrimitiveListType(Class<?> clazz){
        return PRIMITIVE_LIST_MAPPING.containsKey(clazz);
    }

    public static Class<?> getProtobufListType(Class<?> clazz){
        return PRIMITIVE_LIST_MAPPING.get(clazz);
    }

}
