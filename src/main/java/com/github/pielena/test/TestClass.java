package com.github.pielena.test;

import com.github.pielena.annotation.After;
import com.github.pielena.annotation.Before;
import com.github.pielena.annotation.Test;

public class TestClass {

    @Before
    public void init() {
        System.out.println("Before done");
    }

    @Test
    public void doSomethingRight() {
        System.out.println("doSomethingRight successfully done");
    }

    @Test
    public void doSomethingWrong() {
        throw new RuntimeException();
    }

    public void doSomethingWithoutAnnotation() {
        System.out.println("doSomethingWithoutAnnotation successfully done");
    }

    @Test
    private void doSomethingInPrivateMethod() {
        System.out.println("doSomethingInPrivateMethod successfully done");
    }

    @After
    public void clearResources() {
        System.out.println("After done");
        System.out.println("__________________");
    }
}
