package com.mycompany.javafxapplication1.balancer;

class Server {
    private String port;
    private int currentLoad;

    public Server(String port, int currentLoad) {
        this.port = port;
        this.currentLoad = currentLoad;
    }

    public String getPort() {
        return port;
    }

    public int getCurrentLoad() {
        return currentLoad;
        }

    public void incrementLoad() {
        currentLoad++;
    }
}