package http.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.Status;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskAdapter implements JsonDeserializer<Task> {
    @Override
    public Task deserialize(JsonElement jsonElement,
                            Type type,
                            JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        // Проверяем наличие ключей, характерных для конкретного типа задачи
        if (object.has("epicEndTime")) {
            return context.deserialize(object, Epic.class);
        } else if (object.has("idFromEpic")) {
            return context.deserialize(object, Subtask.class);
        } else {
            // В противном случае, это задача типа Task
            Task task = new Task(object.get("name").getAsString(), object.get("description").getAsString());
            if (object.has("id")) {
                task.setId(object.get("id").getAsInt());
            }
            if (object.has("duration")) {
                task.setDuration(object.get("duration").getAsLong());
            }
            if (object.has("status")) {
                String statusStr = object.get("status").getAsString();
                Status status = Status.valueOf(statusStr);
                task.setStatus(status);
            }
            if (object.has("startTime")) {
                String startTimeStr = object.get("startTime").getAsString();
                LocalDateTime startTime = LocalDateTime.parse(startTimeStr, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
                task.setStartTime(startTime);
            }
            return task;
        }
    }
}
