package com.touchsoft.test;

import com.google.gson.Gson;
import com.touchsoft.CommandContainer;
import com.touchsoft.SocketHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SocketHandlerandControllerUnitTest {
    private boolean isClosed;
    private SocketHandler testSocketHandler;
    private String line;
    private ByteArrayOutputStream outputStream;
    private ByteArrayInputStream inputStream;
    private Gson gson;

    public void init() throws IOException{
        gson=new Gson();
        outputStream=new ByteArrayOutputStream();
        isClosed=false;
    }

    public void clean() throws IOException{
        outputStream.close();
        inputStream.close();
    }


    public void registerTest () throws IOException{
        line=gson.toJson(new CommandContainer("/register agent stas"));
        Socket socket=mock(Socket.class);
        inputStream =new ByteArrayInputStream(line.getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        when(socket.getOutputStream()).thenReturn(outputStream);
        when(socket.isClosed()).thenReturn(isClosed);
        doThrow(new IOException()).when(socket).close();
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
    }


}
