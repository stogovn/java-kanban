import manager.Managers;
import manager.TaskManager;
import tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        //Реализуем пользовательский сценарий для ТЗ проекта №6
        Task t1 = new Task("Погладить кота", "Лютик");
        Task t2 = new Task("Поиграть с кошкой", "с Лёпой");
        manager.createTask(t1);
        manager.createTask(t2);
        Epic e1 = new Epic("Оливье", "Сделать оливье");
        manager.createEpic(e1);
        Subtask s1 = new Subtask("Сварить овощи", "Морковь и картофель", e1.getId());
        Subtask s2 = new Subtask("Сварить яйца", "Яйца вкрутую", e1.getId());
        Subtask s3 = new Subtask("Помыть полы", "Два раза, а не один!", e1.getId());
        manager.createSubTask(s1);
        manager.createSubTask(s2);
        manager.createSubTask(s3);
        Epic e2 = new Epic("Epic без подзадач", "Без подзадач");
        manager.createEpic(e2);
        //Просматриваем задачи
        manager.getTaskByID(t1.getId());
        manager.getTaskByID(t2.getId());
        manager.getEpicByID(e1.getId());
        manager.getSubTaskByID(s1.getId());
        manager.getSubTaskByID(s2.getId());
        manager.getSubTaskByID(s3.getId());
        manager.getEpicByID(e2.getId());
        for(Task tasks: manager.getHistory()){
            System.out.println(tasks);
        }
        System.out.println();
        manager.getTaskByID(t1.getId());
        for(Task tasks: manager.getHistory()){
            System.out.println(tasks);
        }
        System.out.println();
        manager.getSubTaskByID(s1.getId());
        for(Task tasks: manager.getHistory()){
            System.out.println(tasks);
        }
        System.out.println();
        //Удаляем задачу, которая есть в истории, и проверяем что при печати она не выводится
        manager.deleteTask(t1.getId());
        for(Task tasks: manager.getHistory()){
            System.out.println(tasks);
        }
        System.out.println();
        //Удаляем эпик с тремя подзадачами и проверяем что из истории удалился и эпик и все его подзадачи
        manager.deleteEpic(e1.getId());
        for(Task tasks: manager.getHistory()){
            System.out.println(tasks);
        }
    }
}
