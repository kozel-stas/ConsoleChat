package com.touchsoft;


import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {

    public static void main(String[] args) throws IOException {
        new ChatServer(8080).run();
    }

}
