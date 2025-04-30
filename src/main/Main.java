package main;

import managerpackage.FileBackedTaskManager;
import managerpackage.InMemoryTaskManager;
import managerpackage.ManagerSaveException;
import managerpackage.TaskManager;
import tasks.*;

import java.io.File;

class Main {
    public static void main(String[] args) {

        TaskManager manager = new InMemoryTaskManager();

        try {
            File file = new File("tasks.csv");
            TaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

            Task task1 = new Task("Задача 1", "Выполнение задачи 1");
            Task task2 = new Task("Задача 2", "Выполнение задачи 2");
            fileBackedTaskManager.addNewTask(task1);
            fileBackedTaskManager.addNewTask(task2);

            Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
            fileBackedTaskManager.addNewEpic(epic1);

            SubTask subtask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
            fileBackedTaskManager.addNewSubTask(subtask1);

            System.out.println("Все задачи после добавления:");
            System.out.println(fileBackedTaskManager.getAllTasks());
            System.out.println(fileBackedTaskManager.getAllEpics());
            System.out.println(fileBackedTaskManager.getAllSubTasks());

            TaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
            System.out.println("\nЗагруженные данные:");
            System.out.println("Задачи: " + loadedManager.getAllTasks());
            System.out.println("Эпики: " + loadedManager.getAllEpics());
            System.out.println("Подзадачи: " + loadedManager.getAllSubTasks());

        } catch (ManagerSaveException e) {
            System.err.println("Ошибка при работе с файлом: " + e.getMessage());
        }


        Task task1 = new Task("Задача 1", "Выполнение задачи  1");
        Task task2 = new Task(null, null);
        manager.addNewTask(task1);
        manager.addNewTask(task2);
        System.out.println("====================");
        Epic epic1 = new Epic("Главная задача 1", "Выполнение гл. задачи  1");
        Epic epic2 = new Epic(null, null);
        manager.addNewEpic(epic1);
        manager.addNewEpic(epic2);
        System.out.println("====================");
        int epic1ID = epic1.getId();
        int epic2ID = epic2.getId();
        SubTask subtask1 = new SubTask("подзадача 1", "Выполнение подзадачи  1", epic1ID);
        SubTask subTask2 = new SubTask(null, null, epic1ID);
        SubTask subTask3 = new SubTask("задача 1", null, epic2ID);
        manager.addNewSubTask(subtask1);
        manager.addNewSubTask(subTask2);
        manager.addNewSubTask(subTask3);
        System.out.println("====================");
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllSubTasks());
        int sub1ID = subtask1.getId();
        int sub2ID = subTask3.getId();

        SubTask updatedTask = new SubTask("1ая задача ", "Выполнение 1", sub1ID, epic1ID, TaskStatus.IN_PROGRESS);
        manager.updateSubTask(updatedTask);
        System.out.println("====================");
        SubTask updatedTask2 = new SubTask("2ая задача", "Выполнение 2", sub2ID, epic2ID, TaskStatus.DONE);
        manager.updateSubTask(updatedTask2);

        System.out.println("Статус эпика");
        System.out.println(manager.getEpic(epic1ID));
        System.out.println(manager.getEpic(epic2ID));
        System.out.println("====================");
        manager.removeSubTaskById(sub2ID);
        manager.addNewSubTask(subTask3);

        System.out.println("Обновление статуса эпика при добавлении новой подзадачи");
        System.out.println(manager.getEpic(epic2ID));
        System.out.println("====================");
        System.out.println(manager.getAllSubTasks());
        manager.removeEpicById(epic1ID);
        System.out.println("====================");
        System.out.println("Список подзадач:");
        System.out.println(manager.getAllSubTasks());
        System.out.println("Проверка удаления");
        manager.clearAllEpics();
        manager.clearAllTasks();
        manager.clearAllSubTasks();
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllSubTasks());


            System.out.println("История");
            System.out.println(manager.getHistory());
            System.out.println();


    }
}
