package org.example.resolver;

import org.example.resolver.prototypes.*;

import java.util.*;

public class ProtobufUtils {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TYPE_MAPPING = new HashMap<>();

    static {
        PRIMITIVE_TYPE_MAPPING.put(boolean.class, int32.class);
        PRIMITIVE_TYPE_MAPPING.put(byte.class, int32.class);
        PRIMITIVE_TYPE_MAPPING.put(short.class, int32.class);
        PRIMITIVE_TYPE_MAPPING.put(int.class, int32.class);
        PRIMITIVE_TYPE_MAPPING.put(long.class, int64.class);
        PRIMITIVE_TYPE_MAPPING.put(Integer.class, int64.class);
        PRIMITIVE_TYPE_MAPPING.put(float.class, float.class);
        PRIMITIVE_TYPE_MAPPING.put(double.class, double.class);
        PRIMITIVE_TYPE_MAPPING.put(java.lang.String.class, string.class);

        // for List types
        PRIMITIVE_TYPE_MAPPING.put(List.class, repeated.class);
        PRIMITIVE_TYPE_MAPPING.put(ArrayList.class, repeated.class);
        PRIMITIVE_TYPE_MAPPING.put(LinkedList.class, repeated.class);
    }

    public static boolean isPrimitiveType(Class<?> clazz) {
        return PRIMITIVE_TYPE_MAPPING.containsKey(clazz);
    }

    public static Class<?> getProtobufType(Class<?> clazz) {
        return PRIMITIVE_TYPE_MAPPING.get(clazz);
    }
}
