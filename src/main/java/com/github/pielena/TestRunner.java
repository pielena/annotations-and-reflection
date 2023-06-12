package com.github.pielena;

import com.github.pielena.annotation.After;
import com.github.pielena.annotation.Before;
import com.github.pielena.annotation.Test;
import com.github.pielena.test.TestClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class TestRunner {

    public static void main(String[] args) {

        Class<?> clazz = TestClass.class;
        run(clazz);
    }

    private static void run(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        checkMultipleAnnotations(methods, Before.class, Test.class, After.class);

        Method[] beforeMethods = getAnnotatedMethods(methods, Before.class);
        Method[] testMethods = getAnnotatedMethods(methods, Test.class);
        Method[] afterMethods = getAnnotatedMethods(methods, After.class);

        int passed = 0;

        for (Method testMethod : testMethods) {
            Object testClassInstance = getInstance(clazz);
            invokeMethods(testClassInstance, beforeMethods);
            try {
                testMethod.invoke(testClassInstance);
                passed++;
            } catch (IllegalAccessException e) {
                System.out.println("Invalid @Test: " + testMethod.getName());
            } catch (InvocationTargetException wrappedExc) {
                Throwable exc = wrappedExc.getCause();
                System.out.println(testMethod.getName() + " FAILED: " + exc);
            } finally {
                invokeMethods(testClassInstance, afterMethods);
            }
        }

        printSummary(testMethods.length, passed);
    }

    private static Object getInstance(Class<?> clazz) {

        Object instance = null;
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return instance;
    }

    private static Method[] getAnnotatedMethods(Method[] methods, Class<? extends Annotation> annotation) {

        return Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(annotation))
                .toArray(Method[]::new);
    }

    private static void invokeMethods(Object testClassInstance, Method... methods) {
        for (Method method : methods) {
            try {
                method.invoke(testClassInstance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private static void printSummary(int total, int passed) {
        System.out.println("Total:  " + total);
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + (total - passed));
    }

    @SafeVarargs
    private static void checkMultipleAnnotations(Method[] methods, Class<? extends Annotation>... annotations) {
        for (Method method : methods) {
            int count = 0;
            for (Class<? extends Annotation> annotation : annotations) {
                if (method.isAnnotationPresent(annotation)) {
                    count++;
                }
            }

            if (count > 1) {
                throw new RuntimeException("There are more than one annotation in method: " + method.getName());
            }
        }
    }
}
