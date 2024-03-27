package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.adapter.LocalDateTimeAdapter;
import http.adapter.TaskAdapter;
import tasks.Task;

import java.time.LocalDateTime;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Task.class, new TaskAdapter());
        return gsonBuilder
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }
}
