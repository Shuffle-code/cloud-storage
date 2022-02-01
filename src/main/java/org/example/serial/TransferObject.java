package org.example.serial;

import java.io.Serializable; // Обязательно для сериализации(перевода в бинарный формат)

public class TransferObject implements Serializable {

    private String tag;
    private String message;


    // добавили два конструктора
    public TransferObject() {
    }

    public TransferObject(String tag, String message) {
        this.tag = tag;
        this.message = message;
    }


    // Добавили геттеры и сеттеры
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    // Добавили toString
    @Override
    public String toString() {
        return "TransferObject{" +
                "tag='" + tag + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
