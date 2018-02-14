package com.touchsoft;

import java.io.*;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        new ClientConect().run();
    }
}
