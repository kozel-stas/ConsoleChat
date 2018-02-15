package com.touchsoft;

import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("Подключится к серверу \n 1-да \n 2-нет");
            if (in.nextInt() == 1)
                new ClientConect().run();
            else break;
        }
    }
}
