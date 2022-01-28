package org.example;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class Client implements Initializable {

    //public TextField textField;
    //public ListView<String> listView;
    private Path clientDir;
    private static final int SIZE = 256;
    public ListView<String> clientView;
    public ListView<String> serverView;
    private DataInputStream is;
    private DataOutputStream os;
    private byte[] buf;
// метод для отправки сообщений: получили текст из текстового поля, очистили текстовое поле, отправили, полностью
//    public void sendMessage(ActionEvent actionEvent) throws IOException {
//        String text = textField.getText();
//        textField.clear();
//        os.writeUTF(text);
//        os.flush();
//    }
    private void readLoop() {
        try {
            while (true) {
//                String msg = is.readUTF(); // wait message
//                Platform.runLater(() -> listView.getItems().add(msg));
                String command = is.readUTF(); // ждет команду
                System.out.println("received: " + command);// wait message
                if (command.equals("#list#")) {
                    Platform.runLater(() -> serverView.getItems().clear()); // Т.к readLoop работает в отдельном потоке
                    int filesCount = is.readInt(); // Создали переменную равную колличеству прочитанному из "Стрима"
                    for (int i = 0; i < filesCount; i++) {
                        String fileName = is.readUTF();
                        Platform.runLater(() -> serverView.getItems().add(fileName)); // Т.к readLoop работает в отдельном потоке
                    }
                }else if (command.equals("#file#")) {
                    Sender.getFile(is, clientDir, SIZE, buf);
                    Platform.runLater(this::updateClientView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateClientView() { // Обновление клиентской "листалки"
        try {
            clientView.getItems().clear(); // Очистили
            Files.list(clientDir) // Собрали файлы на клиенте, добавили по одному
                    .map(p -> p.getFileName().toString())
                    .forEach(f -> clientView.getItems().add(f));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            buf = new byte[SIZE];
            clientDir = Paths.get(System.getProperty("user.home")); // Создали папку (директорию) из которой будем передавать на сервер
            updateClientView();
            Socket socket = new Socket("localhost", 8189); // Создали соединение, указали этот комп + порт, как на сервере
            System.out.println("Network created...");
            is = new DataInputStream(socket.getInputStream()); // Считывает
            os = new DataOutputStream(socket.getOutputStream()); // отправляет
            Thread readThread = new Thread(this::readLoop); // чтение в отдельном потоке(метод readLoop)
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        String fileName = clientView.getSelectionModel().getSelectedItem(); // нашли имя файла из clientView
        Sender.sendFile(fileName, os, clientDir); // Выгрузили через "Отправщик"
    }


    public void download(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem(); // создали(нашли) имя файла из serverView
        os.writeUTF("#get_file#"); // написали команду
        os.writeUTF(fileName); // написали имя файла
        os.flush(); // Отправили всё
    }
}
