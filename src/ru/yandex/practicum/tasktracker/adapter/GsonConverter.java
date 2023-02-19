package ru.yandex.practicum.tasktracker.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;
import java.time.LocalDateTime;

public class GsonConverter {

    public static Gson getGsonTaskConverter() {
       Gson gson = new GsonBuilder()
               .setPrettyPrinting()
               .serializeNulls()
               .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe())
               .registerTypeAdapter(Duration.class, new DurationAdapter())
               .create();
        return gson;
    }
}