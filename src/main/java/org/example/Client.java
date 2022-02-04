package org.example;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.example.model.CloudMessage;
import org.example.model.FileMessage;
import org.example.model.FileRequest;
import org.example.model.ListMessage;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

@Slf4j

public class Client implements Initializable {

    //public TextField textField;
    //public ListView<String> listView;
    private Path clientDir;
    private Path serverDir;
    private static final int SIZE = 256;
    public ListView<String> clientView;
    public ListView<String> serverView;
    public TextField textFieldServer;
    public TextField textFieldClient;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private CloudMessageProcessor processor;

//    private DataInputStream is;
//    private DataOutputStream os;
    private byte[] buf;
// метод для отправки сообщений: получили текст из текстового поля, очистили текстовое поле, отправили, полностью
//    public void sendMessage(ActionEvent actionEvent) throws IOException {
//        String text = textField.getText();
//        textField.clear();
//        os.writeUTF(text);
//        os.flush();
//    }
    private void readLoop() { // Чтение списка на Servere или передача файла из сервера
        try {
            while (true) {
//                readCommand();
//                String msg = is.readUTF(); // wait message
//                Platform.runLater(() -> listView.getItems().add(msg));
//                String command = is.readUTF(); // ждет команду
                CloudMessage message = (CloudMessage) is.readObject();
                log.info("received: {}",message);
                processor.processMessage(message);
//                System.out.println("received: " + command);// wait message
//                switch (message.getType()){
//                    case FILE:
//                        processFileMessage((FileMessage) message);
//                        break;
//
//                    case LIST:
//                        processListMessage((ListMessage) message);
//                        break;
//                }

//                if (command.equals("#list#")) {
//                    Platform.runLater(() -> serverView.getItems().clear()); // Т.к readLoop работает в отдельном потоке
//                    int filesCount = is.readInt(); // Создали переменную равную колличеству прочитанному из "Стрима"
//                    for (int i = 0; i < filesCount; i++) {
//                        String fileName = is.readUTF();
//                        Platform.runLater(() -> serverView.getItems().add(fileName)); // Т.к readLoop работает в отдельном потоке
//
//                    }
//                }else if (command.equals("#file#")) {
//                    Sender.getFile(is, clientDir, SIZE, buf);
//                    Platform.runLater(this::updateClientView);
//
//                }
//                else if (command.equals("#pathDown#")) {
//                    updateClientViewPathDown(); // Т.к readLoop работает в отдельном потоке
//
//                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processListMessage(ListMessage message) {
        Platform.runLater(() -> {
            serverView.getItems().clear();
            serverView.getItems().addAll(message.getFiles());
        });

    }

    private void processFileMessage(FileMessage message) throws IOException {
    Files.write(clientDir.resolve(message.getFileName()), message.getBytes());
    Platform.runLater(this::updateClientView);
    }

    private void readCommand() throws IOException {
        String comm = is.readUTF();
        System.out.println("readCommand: " + comm);
    }

    private void updateClientView() { // Обновление клиентской "листалки"
        try {
            clientView.getItems().clear(); // Очистили
            Files.list(clientDir) // Собрали файлы на клиенте, добавили по одному
                    .map(p -> p.getFileName().toString())
                    .forEach(f -> clientView.getItems().add(f));

            textFieldClient.clear();
            textFieldClient.appendText(String.valueOf(clientDir));
            textFieldServer.clear();
//            textFieldServer.setText("А сюда надо передать serverDir ");
            textFieldServer.appendText(String.valueOf(serverDir));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateClientViewPathDown() { // Обновление клиентской "листалки", после кнотки "на папку ниже"
        try {
            clientView.getItems().clear(); // Очистили
            Files.list(clientDir.resolve("..")) // Собрали файлы на клиенте, добавили по одному
                    .map(p -> p.getFileName().toString())
                    .forEach(f -> clientView.getItems().add(f));
            textFieldClient.clear();
            textFieldClient.appendText(String.valueOf(clientDir.resolve("..")));
//            textFieldServer.clear();
//            textFieldServer.setText("А сюда надо передать serverDir ");
//            textFieldServer.appendText(String.valueOf(serverDir));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            buf = new byte[SIZE];
            clientDir = Paths.get(System.getProperty("user.home")); // Получили путь к  папке (директорию) из которой будем передавать на сервер
            System.out.println(clientDir);
            serverDir = Paths.get("data").toAbsolutePath();
            System.out.println(serverDir);
            updateClientView();
            initMouseLincked();
            processor = new CloudMessageProcessor(clientDir,clientView, serverView);
            Socket socket = new Socket("localhost", 8189); // Создали соединение, указали этот комп + порт, как на сервере
            System.out.println("Network created...");
            os = new ObjectEncoderOutputStream(socket.getOutputStream()); // отправляет
            is = new ObjectDecoderInputStream(socket.getInputStream()); // Считывает

            Thread readThread = new Thread(this::readLoop); // чтение в отдельном потоке(метод readLoop)
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void upload(ActionEvent actionEvent) throws IOException { // отправить
        String fileName = clientView.getSelectionModel().getSelectedItem(); // нашли имя файла из clientView
        os.writeObject(new FileMessage(clientDir.resolve(fileName)));
//        Sender.sendFile(fileName, os, clientDir); // Выгрузили через "Отправщик"
    }


    public void download(ActionEvent actionEvent) throws IOException { // загрузить
        String fileName = serverView.getSelectionModel().getSelectedItem(); // создали(нашли) имя файла из serverView
        os.writeObject(new FileRequest(fileName));
//        os.writeUTF("#get_file#"); // написали команду
//        os.writeUTF(fileName); // написали имя файла
//        os.flush(); // Отправили всё
    }

    public void pathDownClient(ActionEvent actionEvent) throws IOException {
        os.writeUTF("#pathDown#");
//        os.flush();
        System.out.println("Send: " + "#pathDown#");
//        readLoop();
//        Thread readThread_1 = new Thread(this::readLoop); // чтение в отдельном потоке(метод readLoop)
//        readThread_1.setDaemon(true);
//        readThread_1.start();
//        clientDir.resolve("..").toAbsolutePath();
    }

    public void pathDownServer(ActionEvent actionEvent) {

        serverDir.resolve("..").toAbsolutePath();
    }

    private void initMouseLincked() {
        clientView.setOnMouseClicked(e -> {

            if (e.getClickCount() == 2) {
                Path current = clientDir.resolve(getItem());
                if (Files.isDirectory(current)) {
                    clientDir = current;
                    Platform.runLater(this::updateClientView);
                }
            }
        });
        serverView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {

            }
        });
    }
    private String getItem() {
        return clientView.getSelectionModel().getSelectedItem();
    }
}
