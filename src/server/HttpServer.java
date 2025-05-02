package server;

import managerpackage.Managers;
import java.io.IOException;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer(Managers.getDefault(), 8080);
        server.start();
    }
}
