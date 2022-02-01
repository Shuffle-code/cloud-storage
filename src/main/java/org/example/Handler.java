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
    private Path clientDirServer; // Путь к Клиентской директории на сервере
    private DataInputStream is;
    private DataOutputStream os;
    private static final int SIZE = 256;
    private final byte[] buf;
// метод Handler принимает Soket, на сокете поднимает стримы.
    public Handler(Socket socket) throws IOException {
        is = new DataInputStream(socket.getInputStream()); // считывает из потока данные
        os = new DataOutputStream(socket.getOutputStream()); // Класс представляет поток вывода и предназначен для записи данных примитивных типов
        clientDirServer = Paths.get("data"); // Клиентская директория на сервере
        buf = new byte[SIZE];
        sendServerFiles(); // Метод который показывает, какие файлы есть на сервере
    }

    public void sendServerFiles() throws IOException { // Метод который показывает, какие файлы есть на сервере, и отправляет файлы .... на сервер
        List<String> files = Files.list(clientDirServer)
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
                System.out.println("received Handler: " + command);
                if (command.equals("#file#")) { // если нужено отправить файл
                    getFile(is, clientDirServer, SIZE, buf);
                    sendServerFiles(); // получили список через метод и отправили
                } else if (command.equals("#get_file#")) { // Если нужно получить
                    String fileName = is.readUTF();
                    sendFile(fileName, os, clientDirServer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
