import manager.Managers;
import manager.TaskManager;
import tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        //Создаём объекты для задач:
        Task task1 = new Task("Погладить кота", "Лютик");
        Task task2 = new Task("Поиграть с кошкой", "с Лёпой");
        //Создаём сами задачи:
        manager.createTask(task1);//ID=1
        manager.createTask(task2);//ID=2
        //Создаём объекты для эпиков
        Epic epic1 = new Epic("Оливье", "Сделать оливье");
        Epic epic2 = new Epic("Уборка", "Генеральная уборка");
        //Создаём сами эпики:
        manager.createEpic(epic1);//ID=3
        manager.createEpic(epic2);//ID=4
        //Создаём объекты для подзадач:
        Subtask sub1Epic1 = new Subtask("Сварить овощи", "Морковь и картофель", epic1.getId());
        Subtask sub2Epic1 = new Subtask("Сварить яйца", "Яйца вкрутую", epic1.getId());
        Subtask sub1Epic2 = new Subtask("Помыть полы", "Два раза, а не один!", epic2.getId());
        //Создаём сами подзадачи:
        manager.createSubTask(sub1Epic1);//ID=5
        manager.createSubTask(sub2Epic1);//ID=6
        manager.createSubTask(sub1Epic2);//ID=7
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
        System.out.println(manager.getSubtasksOfEpic(epic2.getId()));
        //Меняем статус у созданных задач и подзадач:
        manager.updateTask(new Task(task1.getId(),
                "Погладить кота", "Лютик", Status.IN_PROGRESS));
        manager.updateTask(new Task(task2.getId(),
                "Поиграть с кошкой", "с Лёпой", Status.DONE));
        manager.updateSubtask(new Subtask(sub1Epic1.getId(), "Сварить овощи", "Морковь и картофель",
                Status.DONE, epic1.getId()));
        manager.updateSubtask(new Subtask(sub2Epic1.getId(), "Сварить яйца", "Яйца вкрутую",
                Status.IN_PROGRESS, epic1.getId()));
        manager.updateSubtask(new Subtask(sub1Epic2.getId(), "Помыть полы", "Два раза, а не один!",
                Status.DONE, epic2.getId()));
        System.out.println();
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
        System.out.println();
        //Просматриваем задачи
        manager.getTaskByID(task1.getId());
        manager.getTaskByID(task2.getId());
        manager.getEpicByID(epic1.getId());
        manager.getEpicByID(epic2.getId());
        manager.getSubTaskByID(sub1Epic1.getId());
        manager.getSubTaskByID(sub2Epic1.getId());
        manager.getSubTaskByID(sub1Epic2.getId());
        System.out.println("История просмотров:");
        for(Task tasks: manager.getHistory()){
            System.out.println(tasks);
        }
        //Удаляем одну задачу и один эпик:
        manager.deleteEpic(epic2.getId());
        manager.deleteTask(task2.getId());
        System.out.println();
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
    }
}
