package codesquad;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Server server = new Server(8080);
        server.start();
    }
}
