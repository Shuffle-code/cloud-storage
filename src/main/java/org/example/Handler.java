package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.Sender.getFile;
import static org.example.Sender.sendFile;

public class Handler implements Runnable {
    private Path clientDir; // Путь к Клиентской директории на сервере
    private DataInputStream is;
    private DataOutputStream os;
    private static final int SIZE = 256;
    private final byte[] buf;
// метод Handler принимает Soket, на сокете поднимает стримы.
    public Handler(Socket socket) throws IOException {
        is = new DataInputStream(socket.getInputStream()); // считывает из потока данные
        os = new DataOutputStream(socket.getOutputStream()); // Класс представляет поток вывода и предназначен для записи данных примитивных типов
        clientDir = Paths.get("data"); // Клиентская директория на сервере
        buf = new byte[SIZE];
        sendServerFiles(); // Метод который показывает, какие файлы есть на сервере
    }

    public void sendServerFiles() throws IOException { // Метод который показывает, какие файлы есть на сервере
        List<String> files = Files.list(clientDir)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
        os.writeUTF("#list#"); // Написали команду #list#, без неё не отображается список на сервере
        os.writeInt(files.size());
        for (String file : files) {
            os.writeUTF(file);
        }
        os.flush();
    }

    @Override // переопределили метод Run
    public void run() {
        try {
            while (true) { // вечный цикл.
//                String message = is.readUTF();
//                System.out.println("received: " + message);
//                os.writeUTF(message);
//                os.flush();
                String command = is.readUTF(); // Считали команду из потока
                System.out.println("received: " + command);
                if (command.equals("#file#")) { // если нужен список файлов
                    getFile(is, clientDir, SIZE, buf);
                    sendServerFiles(); // получили список через метод
                } else if (command.equals("#get_file#")) {
                    String fileName = is.readUTF();
                    sendFile(fileName, os, clientDir);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
