package org.example.serial;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TestSerialization {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        TransferObject object = new TransferObject("tag", "message"); // Создали объект
        System.out.println(object);
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("data/object.txt")); // Сериализация
        os.writeObject(object); // Запись бинарного формата в файл

        ObjectInputStream is = new ObjectInputStream(new FileInputStream("data/object.txt"));// Чтение из файла бинарного формата и запись в консоль
        TransferObject object1 = (TransferObject) is.readObject();
        System.out.println(object1);
    }
}
