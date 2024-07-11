package codesquad;

import codesquad.factory.ServerBeanFactory;
import server.Server;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        ServerBeanFactory factory = new ServerBeanFactory();
        Server server = factory.server();
        server.start();
    }
}