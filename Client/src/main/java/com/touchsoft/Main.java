package com.touchsoft;

import java.io.*;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        Socket connect = new Socket("localhost",8080);
        ObjectOutputStream output=new ObjectOutputStream(connect.getOutputStream());
        DataInputStream in=new DataInputStream(connect.getInputStream());
        CommandContainer a=new CommandContainer("/register client Vlad");
        output.writeObject(a);
        output.flush();
        in.readUTF();
        connect.close();
    }
}
