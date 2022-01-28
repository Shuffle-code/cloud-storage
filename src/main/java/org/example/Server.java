package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {

        ServerSocket server = new ServerSocket(8189);
        while (true) {
            Socket socket = server.accept(); // подключили нового клиента. Объявляется Soket на стороне клиента, а сервер воссоздаёт его, получая сигнал на подключение.
            // Тут подключается метод accept(). Искомый ждёт пока кто-либо не захочет подсоединится к нему, и когда это происходит возвращает объект типа Socket,
            // то есть воссозданный клиентский сокет. И вот когда сокет клиента создан на стороне сервера, можно начинать двухстороннее общение.
            System.out.println("New client connected...");
            new Thread(new Handler(socket)).start(); // запустили в отдельном потоке, через класс Handler
        }

    }
}
