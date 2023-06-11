package com.demo.biz;

public class BizFoo {

    private String name;

    public BizFoo() {
        this("Tom");
    }

    public BizFoo(String name) {
        this.name = name;
    }

    public String sayHello(String someone) {
        return "Hello to " + someone + " from " + name;
    }

    public int sayHello(int uid) {
        return uid;
    }

}
