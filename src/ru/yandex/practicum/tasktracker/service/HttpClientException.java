package ru.yandex.practicum.tasktracker.service;

public class HttpClientException extends RuntimeException{

    public HttpClientException(String message) {
        super(message);
    }

}
