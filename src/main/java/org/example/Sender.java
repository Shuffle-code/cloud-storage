package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Sender {

    private Sender() {}

    static void getFile(DataInputStream is, Path clientDir, int size2, byte[] buf) throws IOException {
        String fileName = is.readUTF(); // получили имя
        System.out.println("received file: " + fileName);
        long size = is.readLong(); // получили размер
        try(OutputStream fos = new FileOutputStream(clientDir.resolve(fileName).toFile())) { // К файловому оутпутСтрим в клиенской директории с именем fileName + наверное тело файла дописываем побуферам 256в
            for (int i = 0; i < (size + size2 - 1) / size2; i++) { // (size + size2 - 1) / size2 - для получения кол-ва итераций, кратным количествам считываемых буферов по 256
                int readBytes = is.read(buf); // получили число прочитанных байтов, для последней итерации
                fos.write(buf, 0 , readBytes); // К fos дописываем от нуля до сколько прочитали
            }
        }
    }


    static void sendFile(String fileName, DataOutputStream os, Path clientDir) throws IOException {
        os.writeUTF("#file#");
        os.writeUTF(fileName);
        Path file = clientDir.resolve(fileName); // Нашли нужный путь
        long size = Files.size(file); // Взяли размер (Создали переменную равную размеру файла)
        byte[] bytes = Files.readAllBytes(file); // Создали массив вычитав все байты из file.
        os.writeLong(size);
        os.write(bytes);
        os.flush();
    }

}
