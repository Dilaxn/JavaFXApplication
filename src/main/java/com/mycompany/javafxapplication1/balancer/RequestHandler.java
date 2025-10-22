package com.mycompany.javafxapplication1.balancer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestHandler {

//    private final LoadBalancer scheduler;
//    private final ExecutorService threadPool;

//    public RequestHandler(LoadBalancer scheduler, int threadPoolSize) {
//        this.scheduler = scheduler;
//        this.threadPool = Executors.newFixedThreadPool(threadPoolSize);
//    }
//
//    public String handleRequest(Request request, String algorithm) {
//        String container = scheduler.scheduleRequest(algorithm, request);
//        threadPool.execute(() -> processRequest(request, container));
//        return container;
//    }
//
//    private void processRequest(Request request, String container) {
//        System.out.println("Processing request " + request.getId() + " on container: " + container);
//        try {
//            Thread.sleep((int) (Math.random() * (90 - 30) + 30) * 1000L);
//            System.out.println("Request " + request.getId() + " completed on container: " + container);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            System.err.println("Processing interrupted for request " + request.getId());
//        }
//    }
//
//    public void shutdown() {
//        threadPool.shutdown();
//        System.out.println("Request handler shut down successfully.");
//    }
}


