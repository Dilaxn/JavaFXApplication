package com.mycompany.javafxapplication1.balancer;

import java.util.*;

public class SchedulingAlgorithms {

    private final List<String> storageContainers;

    public SchedulingAlgorithms(List<String> storageContainers) {
        this.storageContainers = storageContainers;
    }

    public String roundRobin() {
        Random rand = new Random();
        return storageContainers.get(rand.nextInt(storageContainers.size()));
    }
}

