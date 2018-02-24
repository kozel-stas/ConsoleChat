package com.touchsoft;

import java.io.IOException;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws IOException {
        new ChatServer(8080).run();
    }

}
