package com.touchsoft;


import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
//        String Li="/register agent Stas";
//        int mark=-1;
//        StringBuilder line = new StringBuilder(Li);
//        mark=line.indexOf(" ");
//        User a=new User("lox");
//        set(a);
//        System.out.println(a.getAgent().getName());
        new ChatServer(8080).run();
    }

}
