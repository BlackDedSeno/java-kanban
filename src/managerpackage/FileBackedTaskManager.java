package managerpackage;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        List<String> lines = new ArrayList<>();
        lines.add("id,type,name,status,description,epic");

        for (Task task : getAllTasks()) {
            lines.add(toString(task));
        }
        for (Epic epic : getAllEpics()) {
            lines.add(toString(epic));
        }
        for (SubTask subTask : getAllSubTasks()) {
            lines.add(toString(subTask));
        }

        try {
            Files.write(file.toPath(), lines);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла", e);
        }
    }

    private String toString(Task task) {
        String type = "TASK";
        String epicId = "";

        if (task instanceof Epic) {
            type = "EPIC";
        } else if (task instanceof SubTask) {
            type = "SUBTASK";
            epicId = String.valueOf(((SubTask) task).getepicID());
        }

        return String.join(",",
                String.valueOf(task.getId()),
                type,
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                task.getStartTime() != null ? task.getStartTime().toString() : "",
                String.valueOf(task.getDuration().toMinutes()),
                epicId
        );
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        int maxId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Task task = fromString(line);

                if (task.getId() > maxId) {
                    maxId = task.getId();
                }

                if (task instanceof Epic) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else if (task instanceof SubTask) {
                    manager.subTasks.put(task.getId(), (SubTask) task);
                } else {
                    manager.tasks.put(task.getId(), task);
                }

                if (task.getStartTime() != null) {
                    manager.getPrioritizedTasks().add(task);
                }
            }

            manager.newId = maxId + 1;

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }

        for (SubTask subTask : manager.subTasks.values()) {
            Epic epic = manager.epics.get(subTask.getepicID());
            if (epic != null) {
                epic.addSubtaskID(subTask.getId());
                manager.updateEpic(epic);
            }
        }

        return manager;
    }

    private static Task fromString(String value) {
        String[] taskData = value.split(",", -1);
        if (taskData.length < 5) {
            throw new ManagerSaveException("Некорректный формат строки: " + value);
        }

        int id = Integer.parseInt(taskData[0]);
        String type = taskData[1];
        String name = taskData[2];
        TaskStatus status = TaskStatus.valueOf(taskData[3]);
        String description = taskData[4];
        LocalDateTime startTime = taskData[5].isEmpty() ? null : LocalDateTime.parse(taskData[5]);
        Duration duration = Duration.ofMinutes(Long.parseLong(taskData[6]));
        String epicId = taskData[7];

        switch (type) {
            case "TASK":
                Task task = new Task(name, description, id, status, startTime, duration);
                task.setID(id);
                return task;

            case "EPIC":
                Epic epic = new Epic(name, description, id);
                epic.setStatus(status);
                epic.setID(id);
                return epic;

            case "SUBTASK":
                if (epicId == null || epicId.isEmpty()) {
                    throw new ManagerSaveException("У подзадачи отсутствует Epic ID");
                }
                SubTask subTask = new SubTask(name, description, id, Integer.parseInt(epicId), status, startTime, duration);
                subTask.setID(id);
                return subTask;

            default:
                throw new ManagerSaveException("Неизвестный тип задачи: " + type);
        }
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void addNewSubTask(SubTask subTask) {
        super.addNewSubTask(subTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void removeSubTaskById(int id) {
        super.removeSubTaskById(id);
        save();
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        save();
    }

    @Override
    public void clearAllSubTasks() {
        super.clearAllSubTasks();
        save();
    }
}