package com.mycompany.javafxapplication1.balancer;

public class Request {
    private final int id;
    private final int priority;

    public Request(int id, int priority) {
        this.id = id;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }
}

