package com.touchsoft;

import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        new ChatServer(8080).run();
    }

}
