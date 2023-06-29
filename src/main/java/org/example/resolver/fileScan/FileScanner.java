package org.example.resolver.fileScan;

import org.example.resolver.protoUtils.ProtobufUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;


public class FileScanner {
    private static final String JAVAEXT = "java.";
    private FileScanner(){}
    public static Set<Class<?>> analyzeImports(Class<?>[] interfaces) {
        Set<Class<?>> dependencies;
        dependencies = new HashSet<>();
        for (Class<?> face : interfaces) {
            if (face == null){
                continue;
            }
            if (!face.isPrimitive() && !face.getPackage().getName().startsWith(JAVAEXT)) {
                dependencies.add(face);
            }
        }
        return dependencies;
    }

    private static Set<Class<?>> analyzeNestedDependency(Type type){
        Set<Class<?>> currSet = new HashSet<>();
        if (type instanceof ParameterizedType){
            Type[] typeArgument = ((ParameterizedType) type).getActualTypeArguments();
            if (typeArgument.length == 1){
                // List Type
                currSet.addAll(analyzeNestedDependency(typeArgument[0]));
            }
            else {
                // Map Type
                currSet.addAll(analyzeNestedDependency(typeArgument[0]));
                currSet.addAll(analyzeNestedDependency(typeArgument[1]));
            }
        }
        else if (type instanceof Class<?> innerClass) {
            currSet.add(innerClass);
        }
        return currSet;
    }

    public static boolean checkNonPrimitive(Class<?> fieldType){
        if (!ProtobufUtils.isPrimitiveType(fieldType)) {
            Package fieldPackage = fieldType.getPackage();
            return fieldPackage != null && !fieldPackage.getName().startsWith(JAVAEXT);
        }
        return false;
    }

    public static Set<Class<?>> analyzeFields(Field[] fields) {
        Set<Class<?>> dependencies;
        dependencies = new HashSet<>();

        for (Field field : fields) {
            Class<?> fieldType = field.getType();

            //checking for Enum Type
            if (fieldType.isEnum()){
                continue;
            }

            // checking for nestedList Type
            if (ProtobufUtils.isPrimitiveListType(fieldType)) {
                Type genericType = field.getGenericType();
                dependencies.addAll(analyzeNestedDependency(genericType));
            }

            // checking for nestedMap Type
            else if (ProtobufUtils.isPrimitiveMapType(fieldType)){
                Type genericType = field.getGenericType();
                dependencies.addAll(analyzeNestedDependency(genericType));
            }

            else if (fieldType.isArray()){
                Class<?> componentType = fieldType.getComponentType();
                while (componentType.isArray()){
                    componentType = componentType.getComponentType();
                }
                if (checkNonPrimitive(componentType)){
                    dependencies.add(componentType);
                }
            }

            else if (fieldType.equals(Object.class)){
                dependencies.add(fieldType);
            }

            else if (checkNonPrimitive(fieldType)){
                dependencies.add(fieldType);
            }
        }
        return dependencies;
    }
}
