package com.touchsoft;

import java.io.*;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        Socket connect = new Socket("localhost",8080);
        DataOutputStream output=new DataOutputStream(connect.getOutputStream());
        DataInputStream in=new DataInputStream(connect.getInputStream());
        output.writeUTF("lox");
        output.flush();
        in.readUTF();
        connect.close();
    }
}
